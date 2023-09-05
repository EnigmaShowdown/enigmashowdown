package com.enigmashowdown.client

import com.enigmashowdown.EnigmaShowdownConstants
import com.enigmashowdown.util.createDefaultMapper
import com.enigmashowdown.util.getLogger
import org.zeromq.ZContext

private object ClientTest {
    val logger = getLogger()
}

fun main(args: Array<String>) {
    val objectMapper = createDefaultMapper()
    ZContext().use { context ->
        ZeroMqBroadcastReceiver(objectMapper, context, "localhost:${EnigmaShowdownConstants.PORT_BROADCAST}").use { receiver ->
            receiver.start()
            ClientTest.logger.info("Started listening")

            while (!Thread.currentThread().isInterrupted) {
                val message = receiver.popMessage()
                if (message != null) {
                    val messageString = objectMapper.writeValueAsString(message)
                    ClientTest.logger.info("Got message:\n$messageString")
                }
                try {
                    Thread.sleep(100)
                } catch (ex: InterruptedException) {
                    Thread.currentThread().interrupt()
                }
            }
        }
    }
}
