package com.enigmashowdown.test

import com.enigmashowdown.ClientType
import com.enigmashowdown.EnigmaShowdownConstants
import com.enigmashowdown.client.ZeroMqRequestClient
import com.enigmashowdown.message.request.ConnectRequest
import com.enigmashowdown.message.request.RequestMessage
import com.enigmashowdown.message.response.ConnectResponse
import com.enigmashowdown.message.response.InvalidRequestResponse
import com.enigmashowdown.message.response.ResponseMessage
import com.enigmashowdown.server.ServerHandler
import com.enigmashowdown.server.ZeroMqServerManager
import com.enigmashowdown.util.createDefaultMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.zeromq.SocketType
import org.zeromq.ZContext
import java.util.UUID

/**
 * This class is used to test how the server responds to invalid messages.
 * This is useful to make sure the server doesn't crash when you send it bad messages
 */
class ZeroMqServerManagerTest {
    @Test
    @Disabled // this test does not run well on GitHub Actions // TODO fix this test running on GitHub Actions later
    fun test() {
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
                val client = ZeroMqRequestClient(objectMapper, context, "localhost", EnigmaShowdownConstants.PORT_SERVER)
                assertTrue(client.send(ConnectRequest(ClientType.PLAYER)).get() is ConnectResponse)

                val badData = "{ bad_json: 5 }"
                val response = context.createSocket(SocketType.REQ).use { socket ->
                    socket.connect("tcp://localhost:${EnigmaShowdownConstants.PORT_SERVER}")
                    socket.sendTimeOut = 800 // ms
                    socket.receiveTimeOut = 800 // ms
                    if (!socket.send(badData.encodeToByteArray())) {
                        throw IllegalStateException("Message failed to send")
                    }
                    val dataReceived: ByteArray = socket.recv() ?: throw IllegalStateException("Message failed to receive")
                    objectMapper.readValue(dataReceived, ResponseMessage::class.java)
                }
                assertEquals(InvalidRequestResponse, response)
            }
        }
    }
}
