package com.enigmashowdown.packet.response

import com.enigmashowdown.packet.Packet
import com.fasterxml.jackson.annotation.JsonSubTypes

@JsonSubTypes(
    value = [
        JsonSubTypes.Type(ConnectResponse::class),
    ],
)
interface ResponseMessage : Packet
