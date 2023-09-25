package com.enigmashowdown.game.conquest

import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.FixtureDef
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.badlogic.gdx.physics.box2d.World
import java.util.UUID

class ConquestPlayer(
    val id: UUID,
    world: World,
) {

    val playerBody = world.createBody(
        BodyDef().apply {
            type = BodyDef.BodyType.DynamicBody
        },
    ).apply {
        createFixture(
            FixtureDef().apply {
                shape = PolygonShape().apply {
                    setAsBox(0.5f, 0.5f)
                }
            },
        )
    }

    fun teleport(x: Float, y: Float) {
        playerBody.setTransform(x, y, 0f)
    }
}
