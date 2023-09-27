package com.enigmashowdown.visual.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.Color
import com.enigmashowdown.ClientType
import com.enigmashowdown.EnigmaShowdownConstants
import com.enigmashowdown.client.BroadcastReceiver
import com.enigmashowdown.client.RequestClient
import com.enigmashowdown.client.ZeroMqBroadcastReceiver
import com.enigmashowdown.client.ZeroMqRequestClient
import com.enigmashowdown.game.conquest.ConquestGameManager
import com.enigmashowdown.game.conquest.map.ConquestLevelInfo
import com.enigmashowdown.game.conquest.state.ConquestStateView
import com.enigmashowdown.message.broadcast.LevelStateBroadcast
import com.enigmashowdown.message.broadcast.TestMessage
import com.enigmashowdown.message.request.ConnectRequest
import com.enigmashowdown.message.request.LevelRequest
import com.enigmashowdown.server.GameServer
import com.enigmashowdown.server.ZeroMqBroadcastManager
import com.enigmashowdown.server.ZeroMqServerManager
import com.enigmashowdown.util.createDefaultMapper
import com.enigmashowdown.util.getLogger
import com.enigmashowdown.visual.render.RenderObject
import com.enigmashowdown.visual.render.Renderable
import com.enigmashowdown.visual.render.RenderableMultiplexer
import com.enigmashowdown.visual.render.ResetRenderable
import com.fasterxml.jackson.databind.ObjectMapper
import org.zeromq.ZContext

fun hostServer(screenChanger: ScreenChanger, renderObject: RenderObject): GameScreen {
    val mapper = createDefaultMapper()
    val context = ZContext()
    // TODO allow user to change port this is hosted on
    val serverHostAddress = "*"
    val broadcastPort = EnigmaShowdownConstants.PORT_BROADCAST
    val serverPort = EnigmaShowdownConstants.PORT_SERVER

    // Server components
    val broadcastManager = ZeroMqBroadcastManager(mapper, context, host = serverHostAddress, port = broadcastPort)
    val gameServer = GameServer(ConquestGameManager(), broadcastManager, broadcastPort, EnigmaShowdownConstants.SUBSCRIBE_TOPIC_ALL)
    val serverManager = ZeroMqServerManager(gameServer, mapper, context, host = serverHostAddress, port = serverPort)

    // Client components
    // TODO make this broadcast receiver communication through a different way (inproc maybe)
    val broadcastReceiver = ZeroMqBroadcastReceiver(mapper, context, "localhost", broadcastPort, EnigmaShowdownConstants.SUBSCRIBE_TOPIC_ALL)
    val requestClient = ZeroMqRequestClient(mapper, context, "localhost", serverPort)

    // Start perform initialization logic for each service
    broadcastManager.start()
    gameServer.start()
    serverManager.start()
    broadcastReceiver.start()

    val onDispose = {
        broadcastManager.close()
        gameServer.close()
        serverManager.close()
        broadcastReceiver.close()
        requestClient.close()
    }

    return GameScreen(mapper, onDispose, requestClient, broadcastReceiver)
}

class GameScreen(
    private val mapper: ObjectMapper,
    private val onDispose: () -> Unit,
    private val requestClient: RequestClient,
    private val broadcastReceiver: BroadcastReceiver,
) : ScreenAdapter() {

    private val renderable: Renderable

    init {
        renderable = RenderableMultiplexer(
            listOf(
                ResetRenderable(Color.BLACK),
//                StageRenderable(uiStage),
            ),
        )
    }

    override fun render(delta: Float) {
        super.render(delta)
        while (true) {
            val message = broadcastReceiver.popMessage() ?: break

            when (message) {
                is LevelStateBroadcast -> {
                    // We make a cast here because GameScreen only supports rendering conquest (as of right now)
                    val conquestStateView = message.gameStateView as ConquestStateView
//                    logger.info("Client got state: $conquestStateView")
                }
                is TestMessage -> {
                    logger.info("Test message: ${message.message}")
                }
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            // TODO remove this once we properly implement level selection
            // This will complete in the background. We won't check for a successful response. We'll just assume it's fine.
            requestClient.send(LevelRequest(ConquestLevelInfo.BETA_1.levelId))
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            // TODO test this with an actual player client
            // This is a hack to tell the server that a player just connected
            requestClient.send(ConnectRequest(ClientType.PLAYER)).handleAsync { message, throwable ->
                logger.info("Got response: $message")
            }
        }

        renderable.render(delta)
    }

    override fun resize(width: Int, height: Int) {
        renderable.resize(width, height)
    }

    override fun dispose() {
        onDispose()
        renderable.dispose()
    }
    override fun hide() {
        dispose()
    }

    companion object {
        private val logger = getLogger()
    }
}
