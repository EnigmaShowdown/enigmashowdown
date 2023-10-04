package com.enigmashowdown.ai

import com.enigmashowdown.EnigmaShowdownConstants
import com.enigmashowdown.client.RequestClient
import com.enigmashowdown.client.framework.ClientFramework
import com.enigmashowdown.client.framework.StateListener
import com.enigmashowdown.game.conquest.action.ConquestAction
import com.enigmashowdown.game.conquest.action.MoveAction
import com.enigmashowdown.game.conquest.state.ConquestStateView
import com.enigmashowdown.message.broadcast.LevelStateBroadcast
import com.enigmashowdown.message.request.KeepAliveRequest
import com.enigmashowdown.message.request.PlayerActionRequest
import com.enigmashowdown.util.getLogger
import com.enigmashowdown.util.toTicks
import java.time.Duration
import java.util.UUID

object SimpleMoveAI : StateListener {
    override fun onBroadcast(playerId: UUID, levelStateBroadcast: LevelStateBroadcast, client: RequestClient) {
        if (levelStateBroadcast.ticksUntilBegin != 0) {
            if (levelStateBroadcast.ticksUntilBegin % Duration.ofSeconds(1).toTicks() == 0) {
                logger.info("Starting in ${levelStateBroadcast.ticksUntilBegin} ticks!")
                client.send(KeepAliveRequest(playerId))
            }
        } else {
            val state = levelStateBroadcast.gameStateView as ConquestStateView
            if (state.tick == 0) {
                logger.info("Starting now!")
            }
            val player = state.players.firstOrNull { it.id == playerId } ?: error("Could not find player $playerId in state: $state")
            if (state.tick % Duration.ofMillis(500).toTicks() == 0) {
                logger.debug("Tick: {} with position: {}", state.tick, player.position)
            }
            client.send(
                PlayerActionRequest(
                    playerId,
                    state.tick,
                    ConquestAction(
                        moveAction = MoveAction(0.0, 1.0),
                    ),
                ),
            )
        }
    }

    private val logger = getLogger()
}

fun main(args: Array<String>) {
    ClientFramework("localhost", EnigmaShowdownConstants.PORT_SERVER, SimpleMoveAI).start()
}
