package com.enigmashowdown.util

import com.badlogic.gdx.math.Vector2

fun Vec2.toVector2() = Vector2(x, y)

fun Vector2.add(vector: Vec2) = add(vector.x, vector.y)
