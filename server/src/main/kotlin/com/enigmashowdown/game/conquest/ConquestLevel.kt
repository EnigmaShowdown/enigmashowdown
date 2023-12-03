package com.enigmashowdown.game.conquest

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.FixtureDef
import com.badlogic.gdx.physics.box2d.World
import com.enigmashowdown.EnigmaShowdownConstants
import com.enigmashowdown.game.GameMove
import com.enigmashowdown.game.conquest.action.ConquestAction
import com.enigmashowdown.game.conquest.collision.ConquestContactListener
import com.enigmashowdown.game.conquest.map.ConquestLevelInfo
import com.enigmashowdown.game.conquest.map.LevelMap
import com.enigmashowdown.game.conquest.state.BarrierTile
import com.enigmashowdown.game.conquest.state.ConquestStateView
import com.enigmashowdown.game.conquest.state.LevelEndStatistic
import com.enigmashowdown.game.conquest.state.LevelEndStatus
import com.enigmashowdown.game.conquest.util.ShapeConstants
import com.enigmashowdown.util.getLogger
import java.util.UUID
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

class ConquestLevel(
    val playerIds: Set<UUID>,
    val conquestLevelInfo: ConquestLevelInfo,
    val levelMap: LevelMap,
) {

    var tick: Int = 0

    val world = World(Vector2.Zero, false)
    val players: List<ConquestPlayer>
    val entities: MutableList<ConquestEntity>
    val levelEndStatistics = mutableListOf<LevelEndStatistic>()

    private val tempVector = Vector2()

    init {
        world.setContactListener(ConquestContactListener())
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
        // entities = players // TODO this line may need to change when we add more entities
        entities = mutableListOf<ConquestEntity>().apply {
            addAll(players)
            add(ConquestCrate(UUID.randomUUID(), world))
            add(ConquestFlag(UUID.randomUUID(), world))
            add(ConquestPressurePlate(UUID.randomUUID(), world))
        }
        for ((mapCoordinate, blockType) in levelMap.fireWaterMap) {
            if (blockType == 1) {
                val newFireBlock = ConquestFire(UUID.randomUUID(), world)
                newFireBlock.teleport(mapCoordinate.x.toFloat() + 0.5f, mapCoordinate.y.toFloat() + 0.5f)
                entities.add(newFireBlock)
            } else if (blockType == 2) {
                val newWaterBlock = ConquestWater(UUID.randomUUID(), world)
                newWaterBlock.teleport(mapCoordinate.x.toFloat() + 0.5f, mapCoordinate.y.toFloat() + 0.5f)
                entities.add(newWaterBlock)
            }
        }

        when (conquestLevelInfo) {
            ConquestLevelInfo.BETA_1 -> {
                for (entity in entities) {
                    when (entity) {
                        is ConquestPlayer -> entity.teleport(50f, 45f)
                        is ConquestCrate -> entity.teleport(55f, 45.75f)
                        is ConquestFlag -> entity.teleport(67f, 48f)
                        is ConquestPressurePlate -> entity.teleport(60f, 45.75f)
                    }
                }
            }

            ConquestLevelInfo.LEVEL_1 -> {
                for (entity in entities) {
                    when (entity) {
                        is ConquestPlayer -> entity.teleport(41f, 50f)
                        is ConquestFlag -> entity.teleport(53f, 63f)
                    }
                }
            }
            ConquestLevelInfo.LEVEL_2 -> {
                for (entity in entities) {
                    when (entity) {
                        is ConquestPlayer -> entity.teleport(43f, 44f)
                        is ConquestFlag -> entity.teleport(66f, 70f)
                    }
                }
            }
            ConquestLevelInfo.LEVEL_3 -> {
                for (entity in entities) {
                    when (entity) {
                        is ConquestPlayer -> entity.teleport(43f, 72f)
                        is ConquestFlag -> entity.teleport(87f, 8f)
                    }
                }
            }
            ConquestLevelInfo.LEVEL_4 -> {
                for (entity in entities) {
                    when (entity) {
                        is ConquestPlayer -> entity.teleport(41f, 50f)
                        is ConquestCrate -> entity.teleport(55f, 50f)
                        is ConquestFlag -> entity.teleport(61f, 60f)
                        is ConquestPressurePlate -> entity.teleport(64f, 58f)
                    }
                }
            }
            ConquestLevelInfo.LEVEL_5 -> {
                for (entity in entities) {
                    when (entity) {
                        is ConquestPlayer -> entity.teleport(41f, 55f)
                        is ConquestFlag -> entity.teleport(75f, 55f)
                    }
                }
            }
            ConquestLevelInfo.LEVEL_6 -> {
                for (entity in entities) {
                    when (entity) {
                        is ConquestPlayer -> entity.teleport(41f, 55f)
                        is ConquestFlag -> entity.teleport(78f, 55f)
                    }
                }
            }
        }
    }

    val gameStateView: ConquestStateView
        get() {
            val entityStates = entities.map { entity ->
                entity.toState()
            }
            return ConquestStateView(tick, entityStates, levelMap.barrierMap.map { BarrierTile(it.key, it.value) }, levelEndStatistics)
        }

    fun move(gameMove: GameMove<ConquestAction>) {
//        logger.info("On level tick: {} moves are: {}", tick, gameMove)

        for (player in players) {
            val action: ConquestAction? = gameMove.actions[player.id]
            if (action != null) {
                action.moveAction?.let { moveAction ->
                    val speed = max(0.0, min(player.currentMaxSpeed, moveAction.speed))
                    val x = cos(moveAction.directionRadians)
                    val y = sin(moveAction.directionRadians)

                    player.playerBody.linearVelocity = tempVector.set((x * speed).toFloat(), (y * speed).toFloat())
//                    logger.info("x: $x, y: $y, current player position: ${player.playerBody.position}")
                }
            }
        }
        // in a 60fps game, you typically do velocityIterations=6 and positionIterations=2
        //   So, for 10tps, just multiply those by 6
        world.step(EnigmaShowdownConstants.TICK_PERIOD_SECONDS, 36, 12)

        for (player in players) {
            if (player.numberOfFlagsBeingTouched > 0) {
                if (levelEndStatistics.none { levelEndStatistic -> levelEndStatistic.playerId == player.id }) {
                    levelEndStatistics.add(
                        LevelEndStatistic(
                            player.id,
                            LevelEndStatus.COMPLETE,
                            tick,
                            // TODO populate these statistic field with non-zero values
                            0,
                            0,
                            0,
                        ),
                    )
                }
            }
        }

        tick++
    }

    companion object {
        val logger = getLogger()
    }
}
