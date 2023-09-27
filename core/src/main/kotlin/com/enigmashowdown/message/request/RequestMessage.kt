package com.enigmashowdown.message.request

import com.enigmashowdown.util.Packet
import com.fasterxml.jackson.annotation.JsonSubTypes

@JsonSubTypes(
    value = [
        JsonSubTypes.Type(ConnectRequest::class),
        JsonSubTypes.Type(LevelRequest::class),
        JsonSubTypes.Type(PlayerActionRequest::class),
        JsonSubTypes.Type(KeepAliveRequest::class),
    ],
)
interface RequestMessage : Packet
