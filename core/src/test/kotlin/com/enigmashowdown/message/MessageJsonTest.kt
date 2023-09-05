package com.enigmashowdown.message

import com.enigmashowdown.util.createDefaultMapper
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class MessageJsonTest {
    @Test
    fun test() {
        val mapper = createDefaultMapper()
        val testMessage = TestMessage("This is a test")
        val data = mapper.writeValueAsBytes(testMessage)
        val deserializedMessage = mapper.readValue(data, BroadcastMessage::class.java)
        assertEquals(testMessage, deserializedMessage)
    }
}
