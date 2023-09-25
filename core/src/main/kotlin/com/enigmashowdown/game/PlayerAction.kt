package com.enigmashowdown.game

import com.enigmashowdown.game.conquest.action.ConquestAction
import com.enigmashowdown.util.Packet
import com.fasterxml.jackson.annotation.JsonSubTypes

@JsonSubTypes(
    value = [
        JsonSubTypes.Type(ConquestAction::class),
    ],
)
interface PlayerAction : Packet
