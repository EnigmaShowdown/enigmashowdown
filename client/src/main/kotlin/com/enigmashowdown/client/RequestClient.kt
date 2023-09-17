package com.enigmashowdown.client

import com.enigmashowdown.packet.request.RequestMessage
import com.enigmashowdown.packet.response.ResponseMessage
import java.util.concurrent.CompletableFuture

interface RequestClient {
    fun send(request: RequestMessage): CompletableFuture<ResponseMessage>
}
