package com.enigmashowdown.client

import com.enigmashowdown.ClientType
import com.enigmashowdown.EnigmaShowdownConstants
import com.enigmashowdown.message.request.ConnectRequest
import com.enigmashowdown.util.createDefaultMapper
import com.enigmashowdown.util.getLogger
import org.zeromq.ZContext
import java.time.Duration

private object ClientCommunicationTest {
    val logger = getLogger()
}

fun main(args: Array<String>) {
    val objectMapper = createDefaultMapper()
    ZContext().use { context ->
        ZeroMqRequestClient(objectMapper, context, "localhost", EnigmaShowdownConstants.PORT_SERVER).use { client ->
            for (i in 1..20) {
                val futureResponse = client.send(ConnectRequest(ClientType.PLAYER))
                val startNanoTime = System.nanoTime()
                val response = futureResponse.get()
                val endNanoTime = System.nanoTime()
                ClientCommunicationTest.logger.info("Took ${Duration.ofNanos(endNanoTime - startNanoTime)}")
                ClientCommunicationTest.logger.info("Got response: $response")
                Thread.sleep(500)
            }
        }
    }
}
