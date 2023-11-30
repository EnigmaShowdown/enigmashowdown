package com.enigmashowdown.client.framework

import com.enigmashowdown.ClientType
import com.enigmashowdown.EnigmaShowdownConstants
import com.enigmashowdown.client.ZeroMqBroadcastReceiver
import com.enigmashowdown.client.ZeroMqRequestClient
import com.enigmashowdown.message.broadcast.LevelStateBroadcast
import com.enigmashowdown.message.request.ConnectRequest
import com.enigmashowdown.message.request.KeepAliveRequest
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
                logger.info("Going to connect to server on port: ${response.broadcastPort} with player ID: ${response.uuid}")
                val playerId = response.uuid
                ZeroMqBroadcastReceiver(objectMapper, context, "localhost", response.broadcastPort, response.subscribeTopic).use { receiver ->
                    receiver.start()

                    while (!Thread.currentThread().isInterrupted) {
                        when (val message = receiver.takeMessage()) {
                            is LevelStateBroadcast -> {
                                if (message.ticksUntilBegin > 0 && message.ticksUntilBegin % EnigmaShowdownConstants.TICKS_PER_SECOND == 0) {
                                    // automatically send a keep alive request during the first few seconds before a game starts
                                    client.send(KeepAliveRequest(playerId))
                                }
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
