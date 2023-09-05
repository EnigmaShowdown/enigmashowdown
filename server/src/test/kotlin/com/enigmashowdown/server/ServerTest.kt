package com.enigmashowdown.server

import com.enigmashowdown.message.TestMessage
import com.enigmashowdown.util.createDefaultMapper
import com.enigmashowdown.util.getLogger
import org.zeromq.ZContext

object ServerTest {
    val logger = getLogger()
}

fun main(args: Array<String>) {
    val objectMapper = createDefaultMapper()
    ZContext().use { context ->
        ZeroMqBroadcastManager(objectMapper, context).use { broadcastManager ->
            broadcastManager.start()
            ServerTest.logger.info("Started server")
            while (true) {
                val message = TestMessage("this is a test")
                broadcastManager.broadcast(message)
                Thread.sleep(500)
            }
        }
    }
}
