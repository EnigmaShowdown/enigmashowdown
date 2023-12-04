package com.enigmashowdown.game.conquest

class ConquestHealth(
    maxHealth: Int,
    initHealth: Int,
) {

    var currentHealth = initHealth
    val totalHealth = maxHealth
    val isAlive: Boolean
        get() = currentHealth > 0

    val damageTaken: Int
        get() = totalHealth - currentHealth
    fun updateHealth(newHealth: Int) {
        currentHealth = newHealth
    }
    fun damage(damageAmount: Int) {
        currentHealth -= damageAmount
    }
}
