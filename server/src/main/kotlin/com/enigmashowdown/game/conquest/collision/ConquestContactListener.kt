package com.enigmashowdown.game.conquest.collision

import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.ContactImpulse
import com.badlogic.gdx.physics.box2d.ContactListener
import com.badlogic.gdx.physics.box2d.Manifold
import com.enigmashowdown.game.conquest.ConquestPlayer
import com.enigmashowdown.game.conquest.ConquestPressurePlate
import com.enigmashowdown.util.getLogger

class ConquestContactListener : ContactListener {
    override fun beginContact(contact: Contact) {
        getPlayerCollidingWithFlag(contact)?.let { player ->
            player.numberOfFlagsBeingTouched++
        }
        getPlatePress(contact)?.let { plate ->
            plate.pressed++
        }
    }

    override fun endContact(contact: Contact) {
        getPlayerCollidingWithFlag(contact)?.let { player ->
            player.numberOfFlagsBeingTouched--
        }
        getPlatePress(contact)?.let { plate ->
            plate.pressed--
        }
    }

    private fun getPlatePress(contact: Contact): ConquestPressurePlate? {
        val dataA = contact.fixtureA.userData
        val dataB = contact.fixtureB.userData
        if (dataA is PressurePlateUserData && dataB is CrateUserData) {
            return dataA.plate
        }
        if (dataB is PressurePlateUserData && dataA is CrateUserData) {
            return dataB.plate
        }
        return null
    }
    private fun getPlayerCollidingWithFlag(contact: Contact): ConquestPlayer? {
        val dataA = contact.fixtureA.userData
        val dataB = contact.fixtureB.userData
        if (dataA is PlayerUserData && dataB is FlagUserData) {
            return dataA.player
        }
        if (dataB is PlayerUserData && dataA is FlagUserData) {
            return dataB.player
        }
        return null
    }

    override fun preSolve(contact: Contact?, oldManifold: Manifold?) { }
    override fun postSolve(contact: Contact?, impulse: ContactImpulse?) { }

    private companion object {
        private val logger = getLogger()
    }
}
