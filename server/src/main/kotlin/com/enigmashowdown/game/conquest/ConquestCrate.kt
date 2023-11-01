package com.enigmashowdown.game.conquest

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.FixtureDef
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.badlogic.gdx.physics.box2d.World
import com.enigmashowdown.game.conquest.state.EntityState
import com.enigmashowdown.game.conquest.state.EntityType
import com.enigmashowdown.util.Vec2
import com.enigmashowdown.util.toVec2
import java.util.UUID

class ConquestCrate(
    override val id: UUID,
    world: World,
) : ConquestEntity {

    val crateBody = world.createBody(
        BodyDef().apply {
            type = BodyDef.BodyType.DynamicBody
            linearDamping = 8.0f
        },
    ).apply {
        createFixture(
            FixtureDef().apply {
                shape = PolygonShape().apply {
                    setAsBox(0.5f, 0.5f, Vector2.Zero, 0.0f)
                }
            },
        )
    }

    override val position: Vec2
        get() = crateBody.position.toVec2()

    override fun teleport(x: Float, y: Float) {
        crateBody.setTransform(x, y, 0f)
    }

    override fun toState(): EntityState {
        return EntityState(id, position, EntityType.CRATE)
    }
}
