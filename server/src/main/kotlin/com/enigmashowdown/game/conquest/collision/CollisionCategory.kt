package com.enigmashowdown.game.conquest.collision

enum class CollisionCategory {
    PLAYER,
    FLAG,
    PRESSURE_PLATE,
    CRATE,
    FIRE,
    WATER,
    // NOTE there is a maximum of 16 allowed categories. This limit is imposed by Box2D
    ;

    val mask: Short
        get() = (1 shl ordinal).toShort()
}
