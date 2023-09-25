package com.enigmashowdown.server

import com.enigmashowdown.EnigmaShowdownConstants
import com.enigmashowdown.message.request.RequestMessage
import com.enigmashowdown.util.getLogger
import com.fasterxml.jackson.databind.ObjectMapper
import org.zeromq.SocketType
import org.zeromq.ZContext
import java.util.concurrent.Executors

class ZeroMqServerManager(
    private val serverHandler: ServerHandler,
    private val objectMapper: ObjectMapper,
    private val context: ZContext,
    private val host: String = "*",
    private val port: Int = EnigmaShowdownConstants.PORT_SERVER,
) : AutoCloseable {
    private val executorService = Executors.newSingleThreadExecutor()

    fun start() {
        executorService.execute(::run)
    }

    private fun run() {
        context.createSocket(SocketType.REP).use { socket ->
            socket.bind("tcp://$host:$port")
            // We could eventually do something like socket.bind("inproc://conquest") here so the bundled visualization can efficiently subscribe to this
            while (!Thread.currentThread().isInterrupted) {
                val receivedData = socket.recv()
                if (receivedData == null) {
                    logger.warn("Received data is null. Something must be wrong")
                    continue
                }
                // TODO error handling for readValue call
                val request = objectMapper.readValue(receivedData, RequestMessage::class.java)
                val response = serverHandler.responseTo(request)
                val responseData = objectMapper.writeValueAsBytes(response)
                socket.send(responseData)
            }
        }
    }
    override fun close() {
        executorService.shutdownNow()
    }

    companion object {
        val logger = getLogger()
    }
}
