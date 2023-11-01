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
import com.enigmashowdown.message.response.ConnectResponse
import com.enigmashowdown.server.GameServer
import com.enigmashowdown.server.ZeroMqBroadcastManager
import com.enigmashowdown.server.ZeroMqServerManager
import com.enigmashowdown.util.createDefaultMapper
import com.enigmashowdown.util.getLogger
import com.enigmashowdown.visual.LevelVisualization
import com.enigmashowdown.visual.render.RenderObject
import com.enigmashowdown.visual.render.Renderable
import com.enigmashowdown.visual.render.RenderableMultiplexer
import com.enigmashowdown.visual.render.RenderableReference
import com.enigmashowdown.visual.render.ResetRenderable
import org.zeromq.ZContext
import java.time.Duration
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import kotlin.math.max
import kotlin.math.min

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

    return GameScreen(renderObject, onDispose, requestClient, broadcastReceiver)
}

fun connectToServer(screenChanger: ScreenChanger, renderObject: RenderObject, host: String, port: Int): GameScreen? {
    val mapper = createDefaultMapper()
    val context = ZContext()

    val requestClient = ZeroMqRequestClient(mapper, context, host, port)
    val responseFuture = requestClient.send(ConnectRequest(ClientType.VISUALIZATION))
    // TODO do this in a separate thread on a loading screen - (this blocks)
    // Also, we should have a way to actually notify the player of what error has occurred
    val response = try {
        responseFuture.get(1000, TimeUnit.MILLISECONDS)
    } catch (ex: TimeoutException) {
        GameScreen.logger.error("Got timeout for host:port = {}:{}", host, port)
        return null
    } catch (ex: ExecutionException) {
        GameScreen.logger.error("Got error", ex)
        return null
    }
    if (response !is ConnectResponse) {
        GameScreen.logger.error("Unexpected response! response: {}", response)
        return null
    }
    // NOTE: We ignore response.uuid for now, but if in the future we actually are required to send a keepalive or tell the server our UUID, we will need to pass it in

    val broadcastReceiver = ZeroMqBroadcastReceiver(mapper, context, host, response.broadcastPort, response.subscribeTopic)

    // Start perform initialization logic for each service
    broadcastReceiver.start()

    val onDispose = {
        broadcastReceiver.close()
        requestClient.close()
    }

    return GameScreen(renderObject, onDispose, requestClient, broadcastReceiver)
}

class GameScreen(
    private val renderObject: RenderObject,
    private val onDispose: () -> Unit,
    private val requestClient: RequestClient,
    private val broadcastReceiver: BroadcastReceiver,
) : ScreenAdapter() {

    private val renderable: Renderable

    private var levelVisualization: LevelVisualization? = null
    private var previousState: LevelStateBroadcast? = null
    private var currentState: LevelStateBroadcast? = null
    private var currentStateReceiveNanos: Long? = null

    init {
        renderable = RenderableMultiplexer(
            listOf(
                ResetRenderable(Color.BLACK),
                RenderableReference { if (previousState == null) null else levelVisualization?.renderable },
//                StageRenderable(uiStage),
            ),
        )
    }

    private fun resetLevel() {
        levelVisualization?.renderable?.dispose()

        levelVisualization = null
        previousState = null
        currentState = null
        currentStateReceiveNanos = null
    }

    private fun processMessages(nowNanos: Long) {
        while (true) {
            val message = broadcastReceiver.popMessage() ?: break

            when (message) {
                is LevelStateBroadcast -> {
                    if (currentState?.levelId != message.levelId) { // if the level has changed since last message (or if there was no current state)
                        resetLevel()
                    }
                    previousState = currentState
                    currentState = message
                    currentStateReceiveNanos = nowNanos
                    if (levelVisualization == null) {
                        val levelInfo = ConquestLevelInfo.fromLevelId(message.levelId) ?: throw IllegalStateException("Invalid level ID provided! message: $message")
                        levelVisualization = LevelVisualization(renderObject, levelInfo.createLevelMap())
                    }

                    // We make a cast here because GameScreen only supports rendering conquest (as of right now)
                    val conquestStateView = message.gameStateView as ConquestStateView
//                    logger.info("Client got state: $conquestStateView")
                }
                is TestMessage -> {
                    logger.info("Test message: ${message.message}")
                }
            }
        }
    }

    override fun render(delta: Float) {
        val nowNanos = System.nanoTime()
        processMessages(nowNanos)

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
        val currentStateReceiveNanos = this.currentStateReceiveNanos
        val durationSinceLastUpdate = if (currentStateReceiveNanos == null) null else Duration.ofNanos(nowNanos - currentStateReceiveNanos)

        val levelVisualization = this.levelVisualization
        if (levelVisualization != null) {
            val previousState = this.previousState
            val currentState = this.currentState!!
            durationSinceLastUpdate!!

            if (previousState != null) {
                // we expect a new state every tick period, so as this becomes closer to 1, we're getting closer to when we should expect a new state
                val percent = durationSinceLastUpdate.toMillis().toFloat() / EnigmaShowdownConstants.TICK_PERIOD_MILLIS.toFloat()
                levelVisualization.update(delta, previousState, currentState, max(0.0f, min(1.0f, percent)))
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
        val logger = getLogger()
    }
}
