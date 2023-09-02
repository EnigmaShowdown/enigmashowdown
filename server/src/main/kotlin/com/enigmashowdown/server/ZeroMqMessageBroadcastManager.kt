package com.enigmashowdown.server

import com.enigmashowdown.EnigmaShowdownConstants
import com.enigmashowdown.message.BroadcastMessage
import com.fasterxml.jackson.databind.ObjectMapper
import org.zeromq.SocketType
import org.zeromq.ZContext
import java.util.concurrent.BlockingDeque
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingDeque

class ZeroMqMessageBroadcastManager(
    private val objectMapper: ObjectMapper,
    private val host: String = "*",
    private val port: Int = EnigmaShowdownConstants.PORT_BROADCAST,
) : BroadcastManager, AutoCloseable {

    private val executorService = Executors.newSingleThreadExecutor()
    private val queue: BlockingDeque<BroadcastMessage> = LinkedBlockingDeque()

    fun start() {
        executorService.execute(::run)
    }

    override fun broadcast(message: BroadcastMessage) {
        if (!queue.offer(message)) {
            throw IllegalStateException("Our blocking queue has reached capacity! This should not happen!")
        }
    }

    override fun close() {
        executorService.shutdownNow() // shutdownNow() should interrupt threads
    }

    private fun run() {
        /*
        TODO We are creating a context here, which is fine for the time being.
          When we eventually combine the server and view applications into a single application,
          we may want the ability to externally manage the context so that we can  use `inproc://...`
         */
        ZContext().use { context ->
            val socket = context.createSocket(SocketType.PUB)
            socket.bind("tcp://$host:$port")
            // We could eventually do something like socket.bind("inproc://conquest") here so the bundled visualization can efficiently subscribe to this
            while (!Thread.currentThread().isInterrupted) {
                val message = queue.takeFirst()
                val bytes = objectMapper.writeValueAsBytes(message) // implicitly uses UTF-8
                socket.send(bytes)
            }
        }
    }
}
