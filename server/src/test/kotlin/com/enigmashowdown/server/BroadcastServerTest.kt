package com.enigmashowdown.server

import com.enigmashowdown.packet.broadcast.TestMessage
import com.enigmashowdown.util.createDefaultMapper
import com.enigmashowdown.util.getLogger
import org.zeromq.ZContext

object BroadcastServerTest {
    val logger = getLogger()
}

fun main(args: Array<String>) {
    val objectMapper = createDefaultMapper()
    ZContext().use { context ->
        ZeroMqBroadcastManager(objectMapper, context).use { broadcastManager ->
            broadcastManager.start()
            BroadcastServerTest.logger.info("Started server")
            while (true) {
                val message = TestMessage("this is a test")
                broadcastManager.broadcast(message)
                Thread.sleep(500)
            }
        }
    }
}
