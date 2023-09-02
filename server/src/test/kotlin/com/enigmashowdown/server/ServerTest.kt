package com.enigmashowdown.server

import com.enigmashowdown.util.createDefaultObjectMapper


class ServerTest {

}

fun main(args: Array<String>) {
    val objectMapper = createDefaultObjectMapper()
    val broadcastManager = ZeroMqMessageBroadcastManager(objectMapper)

    while (true) {

        Thread.sleep(500)
    }
}
