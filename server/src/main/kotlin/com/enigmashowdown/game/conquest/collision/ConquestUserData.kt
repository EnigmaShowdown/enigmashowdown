package com.enigmashowdown.game.conquest.collision

import com.enigmashowdown.game.conquest.ConquestPlayer
import com.enigmashowdown.game.conquest.ConquestPressurePlate

sealed interface ConquestUserData

class PlayerUserData(
    val player: ConquestPlayer,
) : ConquestUserData

object FlagUserData : ConquestUserData

object CrateUserData : ConquestUserData

class PressurePlateUserData(
    val plate: ConquestPressurePlate,
) : ConquestUserData

object DoorUserData : ConquestUserData
