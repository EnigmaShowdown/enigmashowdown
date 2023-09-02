package com.enigmashowdown.client

import com.enigmashowdown.message.BroadcastMessage

interface BroadcastReceiver {
    fun popMessage(): BroadcastMessage?
}
