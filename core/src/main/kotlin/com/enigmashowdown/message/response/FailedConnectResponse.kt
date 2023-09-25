package com.enigmashowdown.message.response

import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("failed-connect-response")
object FailedConnectResponse : ResponseMessage {
    override val type: String
        get() = "failed-connect-response"
}
