package com.enigmashowdown.client

import com.enigmashowdown.packet.request.RequestMessage
import com.enigmashowdown.packet.response.ResponseMessage
import com.fasterxml.jackson.databind.ObjectMapper
import org.zeromq.SocketType
import org.zeromq.ZContext
import java.lang.IllegalStateException
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors

class ZeroMqRequestClient(
    private val objectMapper: ObjectMapper,
    private val context: ZContext,
    private val host: String,
    private val port: Int,
) : RequestClient, AutoCloseable {
    private val executorService = Executors.newFixedThreadPool(5)

    override fun send(request: RequestMessage): CompletableFuture<ResponseMessage> {
        val dataToSend = objectMapper.writeValueAsBytes(request)
        return CompletableFuture.supplyAsync({
            context.createSocket(SocketType.REQ).use { socket ->
                socket.connect("tcp://$host:$port")
                socket.sendTimeOut = 800//ms
                socket.receiveTimeOut = 800 //ms
                if (!socket.send(dataToSend)) {
                    throw IllegalStateException("Message failed to send")
                }
                val dataReceived: ByteArray = socket.recv() ?: throw IllegalStateException("Message failed to receive")
                objectMapper.readValue(dataReceived, ResponseMessage::class.java)
            }
        }, executorService)
    }

    override fun close() {
        executorService.shutdownNow()
    }
}
