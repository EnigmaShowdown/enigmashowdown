package com.enigmashowdown.server

import com.enigmashowdown.ClientType
import com.enigmashowdown.EnigmaShowdownConstants
import com.enigmashowdown.game.GameManager
import com.enigmashowdown.game.GameMove
import com.enigmashowdown.game.GameStateView
import com.enigmashowdown.game.PlayerAction
import com.enigmashowdown.message.broadcast.LevelStateBroadcast
import com.enigmashowdown.message.request.ConnectRequest
import com.enigmashowdown.message.request.LevelRequest
import com.enigmashowdown.message.request.PlayerActionRequest
import com.enigmashowdown.message.request.RequestMessage
import com.enigmashowdown.message.response.ConnectResponse
import com.enigmashowdown.message.response.ErrorResponse
import com.enigmashowdown.message.response.ResponseMessage
import com.enigmashowdown.message.response.SuccessResponse
import com.enigmashowdown.util.getLogger
import java.time.Instant
import java.util.UUID
import java.util.concurrent.BlockingDeque
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.TimeUnit

private class Client(
    val uuid: UUID,
    val clientType: ClientType,
    var lastPing: Instant, // TODO use and update lastPing to do something useful
)
private class RequestedAction<Action : PlayerAction>(
    val playerId: UUID,
    val levelTick: Int,
    val playerAction: Action,
)

class GameServer<StateView : GameStateView, Action : PlayerAction> (
    private val gameManager: GameManager<StateView, Action>,
    private val broadcastManager: BroadcastManager,
    private val broadcastPort: Int,
    private val subscribeTopic: String,
) : AutoCloseable, ServerHandler {

    // region Thread safe variables
    private val scheduledExecutor = Executors.newSingleThreadScheduledExecutor()
    private val actionQueue: BlockingDeque<RequestedAction<Action>> = LinkedBlockingDeque()
    // endregion

    // region Tick variables (Variables updated by tick() and should never be updated by anything else)
    // If you need to access these variables, make sure to update code to make it thread safe
    private var serverTick: Int = 0
    private var levelStartTick: Int? = null
    // endregion

    // region Shared variables
    private val clientMap = mutableMapOf<UUID, Client>()
    private var requestedLevelId: UUID? = null
    // endregion

    fun start() {
        scheduledExecutor.scheduleAtFixedRate(::tick, 0L, EnigmaShowdownConstants.TICK_PERIOD_MILLIS, TimeUnit.MILLISECONDS)
    }
    override fun responseTo(request: RequestMessage): ResponseMessage {
        when (request) {
            is ConnectRequest -> {
                synchronized(clientMap) {
                    val newId: UUID = UUID.randomUUID()
                    clientMap[newId] = Client(newId, request.clientType, Instant.now())
                    return ConnectResponse(newId, broadcastPort, subscribeTopic)
                }
            }

            is LevelRequest -> {
                synchronized(this) {
                    requestedLevelId = request.levelId
                }
                return SuccessResponse
            }

            is PlayerActionRequest -> {
                val action = gameManager.checkPlayerAction(request.playerAction) ?: return ErrorResponse("You requested an invalid action (maybe an action for a different game)")
                val requestedAction = RequestedAction(request.playerId, request.levelTick, action)
                if (!actionQueue.offer(requestedAction)) {
                    throw IllegalStateException("The action queue is full! This should never happen!")
                }
                // If we want to, in the future we could try and determine if the player is sending an early or late action request and return an error response if they are.
                //   For right now, we don't really care. We'll just ignore it in tick()
                return SuccessResponse
            }
        }
        logger.warn("Not set up to handle request: {}", request)
        return ErrorResponse("We are not set up to handle the request of the type: ${request.type}")
    }

    private fun tick() {
        synchronized(this) {
            requestedLevelId?.let { requestedLevelId ->
                gameManager.initializeLevel(requestedLevelId) // TODO allow the resetting of a level
                this.requestedLevelId = null
            }
        }
        val connectedPlayers = synchronized(clientMap) { clientMap.values.filter { it.clientType == ClientType.PLAYER }.map { it.uuid }.toSet() }
        if (gameManager.levelId != null && !gameManager.isLevelStarted) {
            if (gameManager.startLevel(connectedPlayers)) {
                levelStartTick = serverTick + 5 * EnigmaShowdownConstants.TICKS_PER_SECOND // start level in 5 seconds
            } else {
                logger.warn("Unable to start level with connectedPlayers: $connectedPlayers (maybe too many are connected?)")
            }
        }
        if (levelStartTick != null) {
            if (!gameManager.isLevelStarted) {
                logger.info("Game was scheduled to be started, but the game manager does not think the level is started")
                levelStartTick = null
            }
        }
//        broadcastManager.broadcast(TestMessage("GameServer test message"))

        val levelStartTick = levelStartTick
        if (levelStartTick != null) {
            val levelId = gameManager.levelId!!
            val players = gameManager.players!!
            val stateView = gameManager.gameStateView!!

            if (levelStartTick > serverTick) { // level has not yet started
                if (!connectedPlayers.containsAll(players)) {
                    logger.info("One of the players disconnected before the level started")
                    this.levelStartTick = null
                } else {
                    // We are confident we will soon begin the level, so let's send out a broadcast
                    broadcastManager.broadcast(LevelStateBroadcast(levelStartTick - serverTick, levelId, stateView))
                }
                actionQueue.clear()
            } else {
                if (levelStartTick == serverTick) {
                    // This is the first tick of the level. We only need to broadcast this and wait for the player's response
                    actionQueue.clear()
                } else {
                    // If this is not the first tick of this level, then we need to see what actions the players want to do
                    // Note that the game is "behind" a tick until we call gameManager.move
                    val actions = mutableMapOf<UUID, Action>()
                    while (actionQueue.isNotEmpty()) {
                        val actionRequest = actionQueue.pop() // use pop here to assert that the actionQueue is not empty, as this is the only thing that removes elements from it
                        if (stateView.tick != actionRequest.levelTick) {
                            logger.debug("Player: {} sent a request for tick: {} when level tick is: {}", actionRequest.playerId, actionRequest.levelTick, stateView.tick)
                        } else if (!players.contains(actionRequest.playerId)) {
                            logger.debug("Player: {} is trying to make a move, but they aren't apart of this game!", actionRequest.playerId)
                        } else {
                            actions[actionRequest.playerId] = actionRequest.playerAction
                        }
                    }
                    if (actions.size != players.size) {
                        val skippedPlayers = players.filter { !actions.containsKey(it) }
                        logger.debug("These players did not make a move on level tick: {}, skippedPlayers: {}", stateView.tick, skippedPlayers)
                    }
                    gameManager.move(GameMove(actions)) // remember after calling this the gameManager's internal level tick has now incremented.
                }
                broadcastManager.broadcast(LevelStateBroadcast(0, levelId, stateView))
            }
        } else {
            actionQueue.clear()
        }

        serverTick++
    }

    override fun close() {
        scheduledExecutor.shutdownNow()
        logger.info("Closed GameServer")
    }

    companion object {
        val logger = getLogger()
    }
}
