package com.enigmashowdown.message.response

import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("error-response")
data class ErrorResponse(
    val message: String,
) : ResponseMessage {
    override val type: String
        get() = "error-response"
}
