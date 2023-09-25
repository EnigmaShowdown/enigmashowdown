package com.enigmashowdown.game

import java.util.UUID

class GameMove<T : PlayerAction>(
    /** A map representing the action of each player. */
    val actions: Map<UUID, T?>,
)
