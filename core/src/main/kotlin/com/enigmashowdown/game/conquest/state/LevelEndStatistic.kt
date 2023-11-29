package com.enigmashowdown.game.conquest.state

import java.util.UUID

enum class LevelEndStatus {
    COMPLETE,
    FAILED,
}

/**
 * This represents data for a level being failed or complete by a particular player.
 */
data class LevelEndStatistic(
    val playerId: UUID,
    val status: LevelEndStatus,
    val tickEndedOn: Int,
    val damageTaken: Int,
    val damageGiven: Int,
    val enemiesDefeated: Int,
)
