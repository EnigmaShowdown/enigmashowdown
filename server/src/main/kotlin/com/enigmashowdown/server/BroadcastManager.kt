package com.enigmashowdown.server

import com.enigmashowdown.message.broadcast.BroadcastMessage

interface BroadcastManager {
    fun broadcast(message: BroadcastMessage)
}
