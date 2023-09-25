package com.enigmashowdown.game.conquest.state

import com.enigmashowdown.game.GameStateView
import com.enigmashowdown.game.conquest.BarrierType
import com.enigmashowdown.map.MapCoordinate
import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("conquest-state-view")
data class ConquestStateView(
    override val tick: Int,
    val players: List<PlayerState>,
    val barrierMap: Map<MapCoordinate, BarrierType>,
) : GameStateView {
    override val type: String
        get() = "conquest-state-view"
}
