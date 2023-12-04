package com.enigmashowdown.game.conquest

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType
import com.badlogic.gdx.physics.box2d.FixtureDef
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.badlogic.gdx.physics.box2d.World
import com.enigmashowdown.game.conquest.collision.CollisionCategory
import com.enigmashowdown.game.conquest.collision.PlayerUserData
import com.enigmashowdown.game.conquest.state.EntityState
import com.enigmashowdown.game.conquest.state.EntityType
import com.enigmashowdown.game.conquest.state.HealthState
import com.enigmashowdown.util.Vec2
import com.enigmashowdown.util.toVec2
import java.util.UUID

class ConquestPlayer(
    override val id: UUID,
    world: World,
) : ConquestEntity {

    val playerBody = world.createBody(
        BodyDef().apply {
            type = BodyDef.BodyType.DynamicBody
        },
    ).also { body ->
        body.createFixture(
            FixtureDef().apply {
                shape = PolygonShape().apply {
                    setAsBox(0.5f, 0.5f, Vector2.Zero, 0.0f)
                }
                filter.categoryBits = CollisionCategory.PLAYER.mask
            },
        ).apply {
            userData = PlayerUserData(this@ConquestPlayer)
        }
    }
    val currentMaxSpeed: Double
        get() = 10.0

    var numberOfFlagsBeingTouched = 0

    val playerHealth = ConquestHealth(100, 100)

    override val position: Vec2
        get() = playerBody.position.toVec2()

    override fun teleport(x: Float, y: Float) {
        playerBody.setTransform(x, y, 0f)
    }

    override fun toState(): EntityState {
        return EntityState(id, position, EntityType.PLAYER, HealthState(playerHealth.currentHealth, playerHealth.totalHealth))
    }
}
