package com.enigmashowdown.server

import com.enigmashowdown.message.BroadcastMessage

interface BroadcastManager {
    fun broadcast(message: BroadcastMessage)
}
