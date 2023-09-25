package com.enigmashowdown.message.broadcast

import com.enigmashowdown.game.GameStateView
import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("level-state-broadcast")
data class LevelStateBroadcast(
    /** The ticks until the level begins, or 0 if the level has already begun. If value is 0, then you must send an action for that particular tick. */
    val ticksUntilBegin: Int,
    val gameStateView: GameStateView,
) : BroadcastMessage {
    override val type: String
        get() = "level-state-broadcast"
}
