package com.enigmashowdown.client.framework

import com.enigmashowdown.client.RequestClient
import com.enigmashowdown.message.broadcast.LevelStateBroadcast
import java.util.UUID

fun interface StateListener {
    fun onBroadcast(playerId: UUID, levelStateBroadcast: LevelStateBroadcast, client: RequestClient)
}
