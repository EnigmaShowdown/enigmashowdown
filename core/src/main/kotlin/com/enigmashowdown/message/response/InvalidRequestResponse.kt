package com.enigmashowdown.message.response

import com.fasterxml.jackson.annotation.JsonTypeName

/**
 * This response should be returned when bad data is sent to the server
 */
@JsonTypeName("invalid-request-response")
object InvalidRequestResponse : ResponseMessage {
    override val type: String
        get() = "invalid-request-response"
}
