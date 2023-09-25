package com.enigmashowdown.client

import com.enigmashowdown.message.broadcast.BroadcastMessage

interface BroadcastReceiver {
    fun popMessage(): BroadcastMessage?
}
