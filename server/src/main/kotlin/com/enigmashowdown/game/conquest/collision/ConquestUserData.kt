package com.enigmashowdown.game.conquest.collision

import com.enigmashowdown.game.conquest.ConquestPlayer

sealed interface ConquestUserData

class PlayerUserData(
    val player: ConquestPlayer,
) : ConquestUserData

object FlagUserData : ConquestUserData
