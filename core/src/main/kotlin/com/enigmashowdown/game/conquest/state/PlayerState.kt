package com.enigmashowdown.game.conquest.state

import com.enigmashowdown.util.Vec2
import java.util.UUID

data class PlayerState(
    /** The ID of the player, which should also be the client ID of the client controlling this player*/
    val id: UUID,
    val position: Vec2,
)
