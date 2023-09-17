package com.enigmashowdown.packet.request

import com.enigmashowdown.ClientType
import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("connect-request")
data class ConnectRequest(
    val clientType: ClientType
) : RequestMessage {
    override val type: String
        get() = "connect-request"
}
