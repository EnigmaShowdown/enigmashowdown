package com.enigmashowdown.game.conquest

import com.enigmashowdown.game.conquest.state.EntityState
import com.enigmashowdown.util.Vec2
import java.util.UUID

interface ConquestEntity {
    val id: UUID

    val position: Vec2
    fun teleport(x: Float, y: Float)
    fun toState(): EntityState
}
