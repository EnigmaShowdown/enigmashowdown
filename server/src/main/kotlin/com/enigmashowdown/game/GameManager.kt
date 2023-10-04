package com.enigmashowdown.game

import java.util.UUID

interface GameManager<StateView : GameStateView, Action : PlayerAction> {

    /**
     * Checks to see if [playerAction] is of the type [Action]. If it is, it returns it as an [Action], otherwise null is returned
     *
     * The implementation of this function must be thread safe. (This function should be pure, so that shouldn't be a problem)
     */
    fun checkPlayerAction(playerAction: PlayerAction): Action?

    /**
     * Initializes the given level.
     *
     * In the case of an active level that has already started, this will end the current level and load this one.
     *
     * If the levelId is the same as the previous level ID, the current level is still ended and reset.
     *
     * If an invalid levelId is given, it is up to the implementation for what to do. Likely should just log the message and do nothing.
     */
    fun initializeLevel(levelId: UUID)

    /** The currently loaded level */
    val levelId: UUID?

    /** @return true if the level can be started with the given players, false otherwise*/
    fun startLevel(players: Collection<UUID>): Boolean

    /** The players of the started level, or null if the level has not started*/
    val players: Set<UUID>?

    /** A view of the state of the level, or null if the level has not started*/
    val gameStateView: StateView?
    val isLevelStarted: Boolean
        get() = gameStateView != null

    fun move(gameMove: GameMove<Action>)
}
