package com.enigmashowdown.game.conquest

import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.FixtureDef
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.badlogic.gdx.physics.box2d.World
import com.enigmashowdown.game.conquest.collision.CollisionCategory
import com.enigmashowdown.game.conquest.collision.FlagUserData
import com.enigmashowdown.game.conquest.state.EntityState
import com.enigmashowdown.game.conquest.state.EntityType
import com.enigmashowdown.util.Vec2
import com.enigmashowdown.util.toVec2
import java.util.UUID

class ConquestFlag(
    override val id: UUID,
    world: World,
) : ConquestEntity {
    private val body = world.createBody(
        BodyDef().apply {
            // Information about body types: https://box2d.org/documentation/md__d_1__git_hub_box2d_docs_dynamics.html#autotoc_md52
            type = BodyDef.BodyType.StaticBody
        },
    ).also { body ->
        body.createFixture(
            FixtureDef().apply {
                // Sensors: https://box2d.org/documentation/md__d_1__git_hub_box2d_docs_dynamics.html#autotoc_md80
                isSensor = true // there is no collision going on, only checking to see if a player collides
                filter.categoryBits = CollisionCategory.FLAG.mask
                filter.maskBits = CollisionCategory.PLAYER.mask // only collide with the player

                shape = PolygonShape().apply {
                    setAsBox(0.5f, 0.5f)
                }
            },
        ).apply {
            userData = FlagUserData
        }
    }

    override val position: Vec2
        get() = body.position.toVec2()

    override fun teleport(x: Float, y: Float) {
        body.setTransform(x, y, 0f)
    }

    override fun toState(): EntityState {
        return EntityState(id, position, EntityType.FLAG)
    }
}
