package com.enigmashowdown.ai

import com.enigmashowdown.EnigmaShowdownConstants
import com.enigmashowdown.client.RequestClient
import com.enigmashowdown.client.framework.ClientFramework
import com.enigmashowdown.client.framework.StateListener
import com.enigmashowdown.game.conquest.action.ConquestAction
import com.enigmashowdown.game.conquest.action.MoveAction
import com.enigmashowdown.game.conquest.state.ConquestStateView
import com.enigmashowdown.message.broadcast.LevelStateBroadcast
import com.enigmashowdown.message.request.PlayerActionRequest
import com.enigmashowdown.util.getLogger
import java.util.UUID

object MinimalMoveAI : StateListener {
    override fun onBroadcast(playerId: UUID, levelStateBroadcast: LevelStateBroadcast, client: RequestClient) {
        val state = levelStateBroadcast.gameStateView as ConquestStateView
        val player = state.entities.firstOrNull { it.id == playerId } ?: error("player not found")
        // we assume that player is the type player

        client.send(
            PlayerActionRequest(
                playerId,
                state.tick,
                ConquestAction(
                    moveAction = MoveAction.fromDegrees(30.0, 5.0),
                ),
            ),
        )
    }

    private val logger = getLogger()
}

fun main(args: Array<String>) {
    ClientFramework("localhost", EnigmaShowdownConstants.PORT_SERVER, MinimalMoveAI).start()
}
