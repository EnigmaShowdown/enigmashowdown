package com.enigmashowdown.game.conquest

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.FixtureDef
import com.badlogic.gdx.physics.box2d.World
import com.enigmashowdown.game.GameMove
import com.enigmashowdown.game.conquest.action.ConquestAction
import com.enigmashowdown.game.conquest.map.ConquestLevelInfo
import com.enigmashowdown.game.conquest.map.LevelMap
import com.enigmashowdown.game.conquest.state.ConquestStateView
import com.enigmashowdown.game.conquest.state.PlayerState
import com.enigmashowdown.game.conquest.util.ShapeConstants
import com.enigmashowdown.util.Vec2
import com.enigmashowdown.util.getLogger
import java.util.UUID

class ConquestLevel(
    val playerIds: Set<UUID>,
    val conquestLevelInfo: ConquestLevelInfo,
    val levelMap: LevelMap,
) {

    var tick: Int = 0

    val world = World(Vector2.Zero, false)
    val players: List<ConquestPlayer>

    init {
        for ((mapCoordinate, barrierType) in levelMap.barrierMap) {
            world.createBody(
                BodyDef().apply {
                    type = BodyDef.BodyType.StaticBody
                    position.set(mapCoordinate.x.toFloat(), mapCoordinate.y.toFloat())
                },
            ).apply {
                createFixture(
                    FixtureDef().apply {
                        shape = when (barrierType) {
                            BarrierType.WALL_TRIANGLE_SMOOTH_LOWER_LEFT -> ShapeConstants.TRIANGLE_SMOOTH_LOWER_LEFT
                            BarrierType.WALL_TRIANGLE_SMOOTH_LOWER_RIGHT -> ShapeConstants.TRIANGLE_SMOOTH_LOWER_RIGHT
                            BarrierType.WALL_TRIANGLE_SMOOTH_UPPER_LEFT -> ShapeConstants.TRIANGLE_SMOOTH_UPPER_LEFT
                            BarrierType.WALL_TRIANGLE_SMOOTH_UPPER_RIGHT -> ShapeConstants.TRIANGLE_SMOOTH_UPPER_RIGHT
                            else -> ShapeConstants.BOX
                        }
                    },
                )
            }
        }
        players = playerIds.map { ConquestPlayer(it, world) }

        when (conquestLevelInfo) {
            ConquestLevelInfo.BETA_1 -> {
                for (player in players) {
                    player.teleport(50f, 50f)
                }
            }
        }
    }

    val gameStateView: ConquestStateView
        get() {
            val playerStates = players.map { player ->
                val transform = player.playerBody.transform.position!!
                val position = Vec2(transform.x, transform.y)
                PlayerState(player.id, position)
            }
            return ConquestStateView(tick, playerStates, levelMap.barrierMap)
        }

    fun move(gameMove: GameMove<ConquestAction>) {
        logger.info("On level tick: {} moves are: {}", tick, gameMove)

        tick++
    }

    companion object {
        val logger = getLogger()
    }
}
