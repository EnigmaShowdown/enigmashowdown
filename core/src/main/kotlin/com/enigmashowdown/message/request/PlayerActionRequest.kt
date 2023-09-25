package com.enigmashowdown.message.request

import com.enigmashowdown.game.PlayerAction
import com.fasterxml.jackson.annotation.JsonTypeName
import java.util.UUID

@JsonTypeName("player-action-request")
data class PlayerActionRequest(
    val playerId: UUID,
    val levelTick: Int,
    val playerAction: PlayerAction,
) : RequestMessage {
    override val type: String
        get() = "player-action-request"
}
