package com.enigmashowdown.visual

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.enigmashowdown.game.conquest.map.LevelMap
import com.enigmashowdown.game.conquest.state.ConquestStateView
import com.enigmashowdown.message.broadcast.LevelStateBroadcast
import com.enigmashowdown.util.add
import com.enigmashowdown.util.getLogger
import com.enigmashowdown.visual.render.DisposeRenderable
import com.enigmashowdown.visual.render.RenderObject
import com.enigmashowdown.visual.render.Renderable
import com.enigmashowdown.visual.render.RenderableMultiplexer
import com.enigmashowdown.visual.render.StageRenderable
import com.enigmashowdown.visual.render.TiledMapRenderable
import com.enigmashowdown.visual.render.ViewportResizerRenderable
import com.enigmashowdown.visual.update.Updatable

private const val VIEW_WIDTH = 25f
private const val VIEW_HEIGHT = 20f

/**
 * This class should be called by [com.enigmashowdown.visual.screens.GameScreen].
 *
 * This should be used to contain all map rendering logic and any logic for the actual visualization of the state of the game
 */
class LevelVisualization(
    renderObject: RenderObject,
    map: LevelMap,
) : Updatable {
    /** The size in pixels of a single tile */
    private val tileSize = map.tileSize

    private val levelCountdown = LevelCountdown(renderObject)

    val renderable: Renderable

    private val stageViewport: Viewport
    private val tiledViewport: Viewport
    private val stage: Stage

    init {
        val tiledMap = map.tiledMap

        val tiledCamera = OrthographicCamera()
        stageViewport = ExtendViewport(VIEW_WIDTH, VIEW_HEIGHT)
        tiledViewport = ExtendViewport(VIEW_WIDTH * tileSize, VIEW_HEIGHT * tileSize, tiledCamera) // let this viewport handle the camera
        stage = Stage(stageViewport, renderObject.batch)

        renderable = RenderableMultiplexer(
            listOf(
                ViewportResizerRenderable(tiledViewport), // we need a resizer for tiledViewport because it is not managed by a stage like stateViewport is
                TiledMapRenderable(tiledMap, tiledCamera, intArrayOf(map.backgroundLayerIndex, map.floorLayerIndex, map.foregroundLayerIndex)),
                StageRenderable(stage),
                TiledMapRenderable(tiledMap, tiledCamera, intArrayOf(map.topLayerIndex)),
                levelCountdown.renderable,

                DisposeRenderable { map.tiledMap.dispose() },
            ),
        )

        renderable.resize(Gdx.graphics.width, Gdx.graphics.height)
    }

    override fun update(delta: Float, previousState: LevelStateBroadcast, currentState: LevelStateBroadcast, percent: Float) {
//        logger.info("Tick: {} with percent: {}", previousState.gameStateView.tick - previousState.ticksUntilBegin, percent)
        val cameraPosition = averagePlayerPosition(previousState.gameStateView as ConquestStateView)
        cameraPosition.lerp(averagePlayerPosition(currentState.gameStateView as ConquestStateView), percent)
        // TODO cameraPosition is not stable, we need to normalize its movement

        tiledViewport.camera.position.set(cameraPosition.x * tileSize, cameraPosition.y * tileSize, 0f)
        stageViewport.camera.position.set(cameraPosition.x, cameraPosition.y, 0f)
        tiledViewport.camera.update()
        stageViewport.camera.update()

        levelCountdown.update(delta, previousState, currentState, percent)
    }
    private fun averagePlayerPosition(state: ConquestStateView): Vector2 {
        if (state.players.isEmpty()) {
            // We don't ever expect players to be empty, but in the off chance that it is, we'll just return this.
            //   If in the future this is a case we need to properly handle, we may consider giving the level a "default camera position" or just not move the camera at all in this case
            return Vector2()
        }
        var playerCount = 0
        val playerLocationSum = Vector2()
        for (player in state.players) {
            playerLocationSum.add(player.position)
            playerCount++
        }
        return playerLocationSum.set(playerLocationSum.x / playerCount, playerLocationSum.y / playerCount)
    }

    private companion object {
        val logger = getLogger()
    }
}
