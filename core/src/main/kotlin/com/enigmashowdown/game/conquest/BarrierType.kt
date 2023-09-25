package com.enigmashowdown.game.conquest

enum class BarrierType {
    WALL,

    /** Indicates a wall where the lower left portion is not present*/
    WALL_TRIANGLE_SMOOTH_LOWER_LEFT,

    /** Indicates a wall where the lower right portion is not present*/
    WALL_TRIANGLE_SMOOTH_LOWER_RIGHT,

    /** Indicates a wall where the upper left portion is not present*/
    WALL_TRIANGLE_SMOOTH_UPPER_LEFT,

    /** Indicates a wall where the upper right portion is not present*/
    WALL_TRIANGLE_SMOOTH_UPPER_RIGHT,
}
