package com.enigmashowdown.game

import com.enigmashowdown.game.conquest.state.ConquestStateView
import com.enigmashowdown.util.Packet
import com.fasterxml.jackson.annotation.JsonSubTypes

/**
 * Represents a view of the game state.
 * The data in here may not represent everything in the game's state, so it is only a limited view.
 * In most cases, this view should contain all the information the visualization needs.
 *
 * In the future, we may want the implementing game to hide details from players, but not the visualization.
 */
@JsonSubTypes(
    value = [
        JsonSubTypes.Type(ConquestStateView::class),
    ],
)
interface GameStateView : Packet {
    val tick: Int
}
