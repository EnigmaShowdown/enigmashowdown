package com.enigmashowdown.message.request

import com.fasterxml.jackson.annotation.JsonTypeName
import java.util.UUID

@JsonTypeName("keep-alive-request")
data class KeepAliveRequest(
    val clientId: UUID,
) : RequestMessage {
    override val type: String
        get() = "keep-alive-request"
}
