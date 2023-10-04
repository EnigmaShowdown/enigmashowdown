package com.enigmashowdown.message

import com.enigmashowdown.message.broadcast.BroadcastMessage
import com.enigmashowdown.message.broadcast.TestMessage
import com.enigmashowdown.message.response.FailedConnectResponse
import com.enigmashowdown.message.response.ResponseMessage
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

    @Test
    fun testSingleton() {
        // This test fails when KotlinFeature.SingletonSupport is disabled
        val mapper = createDefaultMapper()
        val singleton = FailedConnectResponse
        val data = mapper.writeValueAsBytes(singleton)
        val deserializedMessage = mapper.readValue(data, ResponseMessage::class.java)
        assertEquals(singleton, deserializedMessage)
    }
}
