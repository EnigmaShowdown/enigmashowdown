package com.enigmashowdown.server

import com.enigmashowdown.packet.request.RequestMessage
import com.enigmashowdown.packet.response.ResponseMessage

interface ServerHandler {
    fun responseTo(request: RequestMessage): ResponseMessage
}
