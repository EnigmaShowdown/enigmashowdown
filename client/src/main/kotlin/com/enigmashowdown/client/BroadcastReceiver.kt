package com.enigmashowdown.client

import com.enigmashowdown.message.broadcast.BroadcastMessage

/**
 * Receives messages from a remote BroadcastManager
 */
interface BroadcastReceiver {
    /** Pops a message off of the message stack, or returns null if there is no message*/
    fun popMessage(): BroadcastMessage?
    /** Waits for a message and returns it. (blocking method)*/
    fun takeMessage(): BroadcastMessage
}
