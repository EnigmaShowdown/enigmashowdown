package com.enigmashowdown.game.conquest.state

import com.enigmashowdown.game.GameStateView
import com.enigmashowdown.game.conquest.BarrierType
import com.enigmashowdown.map.MapCoordinate
import com.fasterxml.jackson.annotation.JsonTypeName

data class BarrierTile(
    val coordinate: MapCoordinate,
    val barrierType: BarrierType,
)

@JsonTypeName("conquest-state-view")
data class ConquestStateView(
    override val tick: Int,
    val entities: List<EntityState>,
    val barriers: List<BarrierTile>,
    /**
     * For a level with a single player, this list will only ever be empty, or have one entry.
     *
     * If multiple players are supported in the future, depending on the logic of having multiple players,
     * this list may only be populated once both players have finished the level, or maybe players can complete the level independently of each other.
     */
    val levelEndStatistics: List<LevelEndStatistic>,
) : GameStateView {
    override val type: String
        get() = "conquest-state-view"
}
