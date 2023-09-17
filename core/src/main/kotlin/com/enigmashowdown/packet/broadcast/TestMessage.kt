package com.enigmashowdown.packet.broadcast

import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("test-message")
data class TestMessage(
    val message: String,
) : BroadcastMessage {
    override val type: String
        get() = "test-message"
}
