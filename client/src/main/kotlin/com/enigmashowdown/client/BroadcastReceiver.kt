package com.enigmashowdown.client

import com.enigmashowdown.message.broadcast.BroadcastMessage

/**
 * Receives messages from a remote BroadcastManager
 */
interface BroadcastReceiver {
    fun popMessage(): BroadcastMessage?
}
