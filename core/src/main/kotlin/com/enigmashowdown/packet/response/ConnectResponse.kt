package com.enigmashowdown.packet.response

import com.fasterxml.jackson.annotation.JsonTypeName
import java.util.UUID

@JsonTypeName("connect-response")
data class ConnectResponse(
    val uuid: UUID,
    val broadcastPort: Int,
    val subscribeTopic: String,
) : ResponseMessage {
    override val type: String
        get() = "connect-response"
}
