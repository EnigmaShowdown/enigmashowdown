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

object RadianAI : StateListener {
    override fun onBroadcast(playerId: UUID, levelStateBroadcast: LevelStateBroadcast, client: RequestClient) {
        val state = levelStateBroadcast.gameStateView as ConquestStateView
        val player = state.entities.firstOrNull { it.id == playerId } ?: error("player not found")
        // we assume that player is the type player

        client.send(
            PlayerActionRequest(
                playerId,
                state.tick,

                if (state.tick < 50.0)
                {
                    ConquestAction(
                        moveAction = MoveAction(Math.toRadians(30.0), 5.0)
                    )
                } else if (state.tick < 100.0) {
                    ConquestAction(
                        moveAction = MoveAction(Math.toRadians(90.0), 5.0)
                    )
                } else if (state.tick < 130.0) {
                    ConquestAction(
                        moveAction = MoveAction(Math.toRadians(180.0), 5.0)
                    )
                } else if (state.tick < 160.0) {
                    ConquestAction(
                        moveAction = MoveAction(Math.toRadians(270.0), 5.0)
                    )
                } else {
                    ConquestAction(
                        moveAction = MoveAction(Math.toRadians(180.0), 5.0)
                    )
                }
            ),
        )
    }

    private val logger = getLogger()
}

fun main(args: Array<String>) {
    ClientFramework("localhost", EnigmaShowdownConstants.PORT_SERVER, MinimalMoveAI).start()
}
