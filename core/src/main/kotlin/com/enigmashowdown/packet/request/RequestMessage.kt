package com.enigmashowdown.packet.request

import com.enigmashowdown.packet.Packet
import com.fasterxml.jackson.annotation.JsonSubTypes

@JsonSubTypes(
    value = [
        JsonSubTypes.Type(ConnectRequest::class),
    ],
)
interface RequestMessage : Packet
