package com.enigmashowdown.ai

import com.enigmashowdown.ClientType
import com.enigmashowdown.EnigmaShowdownConstants
import com.enigmashowdown.client.ZeroMqBroadcastReceiver
import com.enigmashowdown.client.ZeroMqRequestClient
import com.enigmashowdown.game.PlayerAction
import com.enigmashowdown.game.conquest.action.ConquestAction
import com.enigmashowdown.game.conquest.action.MoveAction
import com.enigmashowdown.game.conquest.state.ConquestStateView
import com.enigmashowdown.message.broadcast.LevelStateBroadcast
import com.enigmashowdown.message.request.ConnectRequest
import com.enigmashowdown.message.request.KeepAliveRequest
import com.enigmashowdown.message.request.PlayerActionRequest
import com.enigmashowdown.message.response.ConnectResponse
import com.enigmashowdown.util.createDefaultMapper
import com.enigmashowdown.util.getLogger
import com.enigmashowdown.util.toTicks
import org.zeromq.ZContext
import java.time.Duration
import java.util.concurrent.CompletionException

private object SimpleMoveAI {
    val logger = getLogger()
}

fun main(args: Array<String>) {
    val objectMapper = createDefaultMapper()
    ZContext().use { context ->
        ZeroMqRequestClient(objectMapper, context, "localhost", EnigmaShowdownConstants.PORT_SERVER).use { client ->
            val response = try {
                client.send(ConnectRequest(ClientType.PLAYER)).join()
            } catch (ex: CompletionException) {
                SimpleMoveAI.logger.error("Got exception while trying to connect!", ex)
                throw ex
            }
            if (response !is ConnectResponse) {
                SimpleMoveAI.logger.warn("Bad response: {}", response)
                throw IllegalStateException("Got non-success response: $response")
            }
            val playerId = response.uuid
            ZeroMqBroadcastReceiver(objectMapper, context, "localhost", response.broadcastPort, "").use { receiver ->
                receiver.start()

                while (!Thread.currentThread().isInterrupted) {
                    when (val message = receiver.takeMessage()) {
                        is LevelStateBroadcast -> {
                            if (message.ticksUntilBegin != 0) {
                                if (message.ticksUntilBegin % Duration.ofSeconds(1).toTicks() == 0) {
                                    SimpleMoveAI.logger.info("Starting in ${message.ticksUntilBegin} ticks!")
                                    client.send(KeepAliveRequest(playerId))
                                }
                            } else {
                                val state = message.gameStateView as ConquestStateView
                                if (state.tick == 0) {
                                    SimpleMoveAI.logger.info("Starting now!")
                                }
                                val player = state.players.firstOrNull { it.id == playerId } ?: error("Could not find player $playerId in state: $state")
                                if (state.tick % Duration.ofMillis(500).toTicks() == 0) {
                                    SimpleMoveAI.logger.debug("Tick: {} with position: {}", state.tick, player.position)
                                }
                                client.send(PlayerActionRequest(
                                    playerId,
                                    state.tick,
                                    ConquestAction(
                                        moveAction = MoveAction(0.0, 0.3)
                                    )
                                ))
                            }
                        }
                    }

                }
            }
        }
    }
}

