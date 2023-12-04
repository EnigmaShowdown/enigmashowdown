package com.enigmashowdown.game.conquest

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.Fixture
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

class ConquestDoor(
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
                filter.categoryBits = CollisionCategory.DOOR.mask
                filter.maskBits = CollisionCategory.PLAYER.mask

                shape = PolygonShape().apply {
                    setAsBox(1.5f, 1.5f, Vector2.Zero, 0.0f)
                }
            },
        ).apply {
            userData = DoorUserData
        }
    }

    private val doorFixture: Fixture = body.fixtureList.first()
    private var isOpen: Boolean = false

    override val position: Vec2
        get() = body.position.toVec2()

    override fun teleport(x: Float, y: Float) {
        body.setTransform(x, y, 0f)
    }

    override fun toState(): EntityState {
        return EntityState(id, position, EntityType.DOOR, visible = !isOpen)
    }

    // Enables player collision
    private fun disablePlayerCollision() {
        val filter = doorFixture.filterData
        filter.maskBits = 0
        doorFixture.filterData = filter
    }

    // Disables player collision
    private fun enablePlayerCollision() {
        val filter = doorFixture.filterData
        filter.maskBits = CollisionCategory.PLAYER.mask
        doorFixture.filterData = filter
    }

    // Opens and closes the door
    fun toggleDoor() {
        isOpen = !isOpen
        if (isOpen) {
            disablePlayerCollision()
        } else {
            enablePlayerCollision()
        }
    }
}
