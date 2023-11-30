package com.enigmashowdown.game.conquest

import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.FixtureDef
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.badlogic.gdx.physics.box2d.World
import com.enigmashowdown.game.conquest.collision.CollisionCategory
import com.enigmashowdown.game.conquest.collision.DoorUserData
import com.enigmashowdown.game.conquest.state.EntityState
import com.enigmashowdown.game.conquest.state.EntityType
import com.enigmashowdown.util.Vec2
import com.enigmashowdown.util.toVec2
import java.util.UUID

class ConquestDoor (
    override val id: UUID,
    world: World,
) : ConquestEntity {
    private val body = world.createBody(
        BodyDef().apply {
            type = BodyDef.BodyType.StaticBody
        },
    ).also { body ->
        body.createFixture(
            FixtureDef().apply {
                isSensor = false
                filter.categoryBits = CollisionCategory.DOOR.mask

                shape = PolygonShape().apply {
                    setAsBox(0.5f, 0.5f)
                }
            },
        ).apply {
            userData = DoorUserData
        }
    }

    override val position: Vec2
        get() = body.position.toVec2()

    override fun teleport(x: Float, y: Float) {
        body.setTransform(x, y, 0f)
    }

    override fun toState(): EntityState {
        return EntityState(id, position, EntityType.DOOR)
    }
}
