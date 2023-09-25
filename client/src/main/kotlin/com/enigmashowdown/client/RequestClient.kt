package com.enigmashowdown.client

import com.enigmashowdown.message.request.RequestMessage
import com.enigmashowdown.message.response.ResponseMessage
import java.util.concurrent.CompletableFuture

interface RequestClient {
    fun send(request: RequestMessage): CompletableFuture<ResponseMessage>
}
