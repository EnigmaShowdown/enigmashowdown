package com.enigmashowdown.game.conquest.action

import kotlin.math.atan2
import kotlin.math.sqrt

data class MoveAction(
    val directionRadians: Double,
    /** The speed to move at. Should be a value between [0, max speed], where max speed is known from the state of the game*/
    val speed: Double,
) {
    companion object {
        fun fromVec(x: Double, y: Double): MoveAction {
            val radians = atan2(y, x)
            return MoveAction(radians, sqrt(x * x + y * y))
        }
        fun fromDegrees(directionDegrees: Double, speed: Double) = MoveAction(Math.toRadians(directionDegrees), speed)
    }
}
