package com.enigmashowdown.server

import com.enigmashowdown.message.request.RequestMessage
import com.enigmashowdown.message.response.ResponseMessage

fun interface ServerHandler {
    fun responseTo(request: RequestMessage): ResponseMessage
}
