package com.enigmashowdown.visual

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.enigmashowdown.game.conquest.map.LevelMap
import com.enigmashowdown.game.conquest.state.ConquestStateView
import com.enigmashowdown.game.conquest.state.EntityType
import com.enigmashowdown.message.broadcast.LevelStateBroadcast
import com.enigmashowdown.util.add
import com.enigmashowdown.util.getLogger
import com.enigmashowdown.visual.render.DisposeRenderable
import com.enigmashowdown.visual.render.RenderObject
import com.enigmashowdown.visual.render.Renderable
import com.enigmashowdown.visual.render.RenderableMultiplexer
import com.enigmashowdown.visual.render.RenderableReference
import com.enigmashowdown.visual.render.StageRenderable
import com.enigmashowdown.visual.render.TiledMapRenderable
import com.enigmashowdown.visual.render.ViewportResizerRenderable
import com.enigmashowdown.visual.update.Updatable
import kotlin.math.max
import kotlin.math.min

private const val VIEW_WIDTH = 30f
private const val VIEW_HEIGHT = 20f

/**
 * This class should be called by [com.enigmashowdown.visual.screens.GameScreen].
 *
 * This should be used to contain all map rendering logic and any logic for the actual visualization of the state of the game
 */
class LevelVisualization(
    private val renderObject: RenderObject,
    map: LevelMap,
    private val doReturnHome: () -> Unit,
    private val doRestartLevel: () -> Unit,
) : Updatable {
    /** The size in pixels of a single tile */
    private val tileSize = map.tileSize
    private val levelCountdown = LevelCountdown(renderObject)
    private var endDisplay: LevelEndDisplay? = null
    private val entitySpriteManager: EntitySpriteManager
    private val healthBar = HealthClassManager(renderObject)

    val renderable: Renderable

    private val stageViewport: ExtendViewport
    private val tiledViewport: ExtendViewport
    private val stage: Stage

    /** Represents how far to zoom. 10 is 100% zoom (normal). 20 is 200% zoom, etc*/
    private var zoomValue: Int = 10

    init {
        val tiledMap = map.tiledMap

        val tiledCamera = OrthographicCamera()
        stageViewport = ExtendViewport(VIEW_WIDTH, VIEW_HEIGHT)
        tiledViewport = ExtendViewport(VIEW_WIDTH * tileSize, VIEW_HEIGHT * tileSize, tiledCamera) // let this viewport handle the camera
        stage = Stage(stageViewport, renderObject.batch)

        entitySpriteManager = EntitySpriteManager(renderObject, stage)

//        stage.isDebugAll = true // turn on if you are having trouble figuring out what the bounds of your actor is
        renderable = RenderableMultiplexer(
            listOf(
                ViewportResizerRenderable(tiledViewport), // we need a resizer for tiledViewport because it is not managed by a stage like stageViewport is
                TiledMapRenderable(tiledMap, tiledCamera, intArrayOf(map.backgroundLayerIndex, map.floorLayerIndex, map.foregroundLayerIndex)),
                StageRenderable(stage),
                TiledMapRenderable(tiledMap, tiledCamera, intArrayOf(map.topLayerIndex)),
                levelCountdown.renderable,
                RenderableReference { endDisplay?.renderable },

                DisposeRenderable { map.tiledMap.dispose() },
                DisposeRenderable(entitySpriteManager::dispose),
                healthBar.renderable,
            ),
        )

        renderable.resize(Gdx.graphics.width, Gdx.graphics.height)
    }

    override fun update(delta: Float, previousState: LevelStateBroadcast, currentState: LevelStateBroadcast, percent: Float) {
        if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) || Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT)) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.EQUALS)) { // plus
                zoomValue++
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.MINUS)) {
                zoomValue--
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_0)) {
                zoomValue = 10
            }
            zoomValue = max(2, min(20, zoomValue))
            scale(zoomValue / 10.0f)
        }

//        logger.info("Tick: {} with percent: {}", previousState.gameStateView.tick - previousState.ticksUntilBegin, percent)
        val cameraPosition = averagePlayerPosition(previousState.gameStateView as ConquestStateView)
        cameraPosition.lerp(averagePlayerPosition(currentState.gameStateView as ConquestStateView), percent)

        tiledViewport.camera.position.set(cameraPosition.x * tileSize, cameraPosition.y * tileSize, 0f)
        stageViewport.camera.position.set(cameraPosition.x, cameraPosition.y, 0f)
        tiledViewport.apply()
        stageViewport.apply()

        levelCountdown.update(delta, previousState, currentState, percent)
        entitySpriteManager.update(delta, previousState, currentState, percent)

        val gameState = (previousState.gameStateView as ConquestStateView)

        for (entity in gameState.entities) {
            if (entity.entityType == EntityType.PLAYER) {
                healthBar.update(entity.health!!.health, entity.health!!.totalHealth)
            }
        }

        if (endDisplay == null && gameState.levelEndStatistics.isNotEmpty()) {
            require(gameState.levelEndStatistics.size == 1) { "We currently don't have logic implemented for multiple level end statistics" }
            val statistic = gameState.levelEndStatistics[0]
            endDisplay = LevelEndDisplay(renderObject, statistic.status, statistic.tickEndedOn, 0, 0, 0, doReturnHome, doRestartLevel)
        } else if (endDisplay != null && gameState.levelEndStatistics.isEmpty()) {
            endDisplay = null
        }

        endDisplay?.let { endDisplay ->
            endDisplay.update(delta, previousState, currentState, percent)
            Gdx.input.inputProcessor = endDisplay.inputProcessor
        }
    }
    private fun averagePlayerPosition(state: ConquestStateView): Vector2 {
        var playerCount = 0
        val playerLocationSum = Vector2()
        for (entity in state.entities) {
            if (entity.entityType == EntityType.PLAYER) {
                playerLocationSum.add(entity.position)
                playerCount++
            }
        }
        if (playerCount == 0) {
            // We don't ever expect players to be empty, but in the off chance that it is, we'll just return this.
            //   If in the future this is a case we need to properly handle, we may consider giving the level a "default camera position" or just not move the camera at all in this case
            return Vector2()
        }
        return playerLocationSum.set(playerLocationSum.x / playerCount, playerLocationSum.y / playerCount)
    }

    private fun scale(zoom: Float) {
        tiledViewport.minWorldWidth = VIEW_WIDTH * tileSize / zoom
        tiledViewport.minWorldHeight = VIEW_HEIGHT * tileSize / zoom

        stageViewport.minWorldWidth = VIEW_WIDTH / zoom
        stageViewport.minWorldHeight = VIEW_HEIGHT / zoom

        tiledViewport.update(Gdx.graphics.width, Gdx.graphics.height)
        stageViewport.update(Gdx.graphics.width, Gdx.graphics.height)
    }

    private companion object {
        val logger = getLogger()
    }
}
