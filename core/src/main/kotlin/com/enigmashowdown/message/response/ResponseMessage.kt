package com.enigmashowdown.message.response

import com.enigmashowdown.util.Packet
import com.fasterxml.jackson.annotation.JsonSubTypes

@JsonSubTypes(
    value = [
        JsonSubTypes.Type(ConnectResponse::class),
        JsonSubTypes.Type(FailedConnectResponse::class),
        JsonSubTypes.Type(InvalidRequestResponse::class),
    ],
)
interface ResponseMessage : Packet
