package com.enigmashowdown.game.conquest.action

data class MoveAction(
    val directionRadians: Double,
    /** The speed to move at. Should be a value between [0, max speed], where max speed is known from the state of the game*/
    val speed: Double,
)
