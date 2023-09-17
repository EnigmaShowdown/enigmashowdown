package com.enigmashowdown.server

import com.enigmashowdown.packet.broadcast.BroadcastMessage

interface BroadcastManager {
    fun broadcast(message: BroadcastMessage)
}
