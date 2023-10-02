package com.enigmashowdown.visual.update

import com.enigmashowdown.message.broadcast.LevelStateBroadcast

/**
 * Represents something that can be updated with the current state of the game
 *
 * NOTE: This class passes [LevelStateBroadcast] objects.
 * For the time being, it can be assumed that [LevelStateBroadcast.gameStateView] is a [com.enigmashowdown.game.conquest.state.ConquestStateView].
 * You may cast to this value as you please.
 *
 * The fact that this interface is implemented by a class may not signify much about that class.
 * This interface is used for creating a common signature for the update method, and usually nothing more.
 */
interface Updatable {
    /**
     * @param delta The amount of time in seconds since the last frame
     * @param previousState The previous state
     * @param currentState The current state
     * @param percent A value from 0 to 1. A value of 0 means you should render [previousState] and a value of 1 means you should render [currentState].
     */
    fun update(delta: Float, previousState: LevelStateBroadcast, currentState: LevelStateBroadcast, percent: Float)
}
