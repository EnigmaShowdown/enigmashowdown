package com.enigmashowdown.client

import com.enigmashowdown.packet.broadcast.BroadcastMessage

interface BroadcastReceiver {
    fun popMessage(): BroadcastMessage?
}
