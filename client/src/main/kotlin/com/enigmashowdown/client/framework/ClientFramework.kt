package com.enigmashowdown.client.framework

import com.enigmashowdown.ClientType
import com.enigmashowdown.client.ZeroMqBroadcastReceiver
import com.enigmashowdown.client.ZeroMqRequestClient
import com.enigmashowdown.message.broadcast.LevelStateBroadcast
import com.enigmashowdown.message.request.ConnectRequest
import com.enigmashowdown.message.response.ConnectResponse
import com.enigmashowdown.util.createDefaultMapper
import com.enigmashowdown.util.getLogger
import org.zeromq.ZContext
import java.util.concurrent.CompletionException

class ClientFramework(
    private val host: String,
    private val serverPort: Int,
    private val stateListener: StateListener,
) {

    fun start() {
        val objectMapper = createDefaultMapper()
        ZContext().use { context ->
            ZeroMqRequestClient(objectMapper, context, host, serverPort).use { client ->
                val response = try {
                    client.send(ConnectRequest(ClientType.PLAYER)).join()
                } catch (ex: CompletionException) {
                    logger.error("Got exception while trying to connect!", ex)
                    throw ex
                }
                if (response !is ConnectResponse) {
                    logger.warn("Bad response: {}", response)
                    throw IllegalStateException("Got non-success response: $response")
                }
                val playerId = response.uuid
                ZeroMqBroadcastReceiver(objectMapper, context, "localhost", response.broadcastPort, "").use { receiver ->
                    receiver.start()

                    while (!Thread.currentThread().isInterrupted) {
                        when (val message = receiver.takeMessage()) {
                            is LevelStateBroadcast -> {
                                stateListener.onBroadcast(playerId, message, client)
                            }
                        }
                    }
                }
            }
        }
    }

    private companion object {
        private val logger = getLogger()
    }
}
