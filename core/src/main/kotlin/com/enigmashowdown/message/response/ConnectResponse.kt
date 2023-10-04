package com.enigmashowdown.message.response

import com.fasterxml.jackson.annotation.JsonTypeName
import java.util.UUID

/**
 * This response is sent after a [com.enigmashowdown.message.request.ConnectRequest] is sent.
 * At the time of writing, the data in this response is tightly coupled to our ZeroMq implementations.
 * That is OK for the time being.
 */
@JsonTypeName("connect-response")
data class ConnectResponse(
    val uuid: UUID,
    val broadcastPort: Int,
    /** The topic to subscribe to. For the time being, this should always be an empty string until the client-side is able to accept a topic and then data format.*/
    val subscribeTopic: String,
) : ResponseMessage {
    override val type: String
        get() = "connect-response"
}
