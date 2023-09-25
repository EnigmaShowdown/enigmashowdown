package com.enigmashowdown.client

import com.enigmashowdown.message.broadcast.BroadcastMessage
import com.enigmashowdown.util.getLogger
import com.fasterxml.jackson.databind.ObjectMapper
import org.zeromq.SocketType
import org.zeromq.ZContext
import java.util.concurrent.BlockingDeque
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingDeque

class ZeroMqBroadcastReceiver(
    private val objectMapper: ObjectMapper,
    private val context: ZContext,
    private val host: String,
    private val port: Int,
    private val subscribeTopic: String,
) : BroadcastReceiver, AutoCloseable {

    private val executorService = Executors.newSingleThreadExecutor()
    private val queue: BlockingDeque<BroadcastMessage> = LinkedBlockingDeque()

    override fun popMessage(): BroadcastMessage? {
        return queue.poll()
    }

    fun start() {
        executorService.execute(::run)
    }

    private fun run() {
        context.createSocket(SocketType.SUB).use { socket ->
            socket.connect("tcp://$host:$port") // TODO doing $host:$port won't work for IPv6 addresses
            socket.setLinger(0)
            socket.subscribe(subscribeTopic.toByteArray(Charsets.UTF_8))
            while (true) {
                val data = socket.recv()
                val message = objectMapper.readValue(data, BroadcastMessage::class.java)
                if (!queue.offer(message)) {
                    throw IllegalStateException("The queue is full! This should not happen!")
                }
            }
        }
    }

    override fun close() {
        executorService.shutdownNow() // shutdownNow() should interrupt threads
        logger.info("close for broadcast receiver")
    }

    companion object {
        private val logger = getLogger()
    }
}
