package com.enigmashowdown.game.conquest.util

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.ChainShape
import com.badlogic.gdx.physics.box2d.PolygonShape

object ShapeConstants {
    val TRIANGLE_SMOOTH_LOWER_LEFT = PolygonShape().apply {
        set(
            floatArrayOf(
                0.0f,
                1.0f,
                1.0f,
                1.0f,
                1.0f,
                0.0f,
            ),
        )
    }
    val TRIANGLE_SMOOTH_LOWER_RIGHT = PolygonShape().apply {
        set(
            floatArrayOf(
                0.0f,
                0.0f,
                0.0f,
                1.0f,
                1.0f,
                1.0f,
            ),
        )
    }
    val TRIANGLE_SMOOTH_UPPER_LEFT = PolygonShape().apply {
        set(
            arrayOf(
                Vector2(0.0f, 0.0f),
                Vector2(1.0f, 1.0f),
                Vector2(1.0f, 0.0f),
            ),
        )
    }
    val TRIANGLE_SMOOTH_UPPER_RIGHT = PolygonShape().apply {
        set(
            arrayOf(
                Vector2(0.0f, 1.0f),
                Vector2(0.0f, 0.0f),
                Vector2(1.0f, 0.0f),
            ),
        )
    }
    val BOX = ChainShape().apply {
        createLoop(
            floatArrayOf(
                0.0f,
                0.0f,
                1.0f,
                0.0f,
                1.0f,
                1.0f,
                0.0f,
                1.0f,
            ),
        )
    }
}
