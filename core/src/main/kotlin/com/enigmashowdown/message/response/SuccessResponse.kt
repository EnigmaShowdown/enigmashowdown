package com.enigmashowdown.message.response

import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("success-response")
object SuccessResponse : ResponseMessage {
    override val type: String
        get() = "success-response"
}
