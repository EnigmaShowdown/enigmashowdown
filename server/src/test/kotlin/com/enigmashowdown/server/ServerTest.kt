package com.enigmashowdown.server

import com.enigmashowdown.EnigmaShowdownConstants
import com.enigmashowdown.message.request.ConnectRequest
import com.enigmashowdown.message.request.RequestMessage
import com.enigmashowdown.message.response.ConnectResponse
import com.enigmashowdown.message.response.ResponseMessage
import com.enigmashowdown.util.createDefaultMapper
import com.enigmashowdown.util.getLogger
import org.zeromq.ZContext
import java.util.*

object ServerTest {
    val logger = getLogger()
}

fun main(args: Array<String>) {
    val objectMapper = createDefaultMapper()
    val serverHandler = object : ServerHandler {
        override fun responseTo(request: RequestMessage): ResponseMessage {
            if (request is ConnectRequest) {
                return ConnectResponse(UUID.randomUUID(), EnigmaShowdownConstants.PORT_BROADCAST, "")
            }
            throw IllegalArgumentException("Unknown request: $request")
        }
    }
    ZContext().use { context ->
        ZeroMqServerManager(serverHandler, objectMapper, context).use { serverManager ->
            serverManager.start()
            ServerTest.logger.info("Started server")
            while (true) {
                Thread.sleep(500)
            }
        }
    }
}
