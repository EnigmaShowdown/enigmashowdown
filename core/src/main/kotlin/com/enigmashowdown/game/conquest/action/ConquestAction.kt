package com.enigmashowdown.game.conquest.action

import com.enigmashowdown.game.PlayerAction
import com.fasterxml.jackson.annotation.JsonTypeName

/**
 * This encapsulates all the actions the player is taking during a single tick
 */
@JsonTypeName("conquest-action")
data class ConquestAction(
    val moveAction: MoveAction? = null,
) : PlayerAction {
    override val type: String
        get() = "conquest-action"
}
