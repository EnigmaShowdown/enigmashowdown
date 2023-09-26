package com.enigmashowdown.message.broadcast

import com.enigmashowdown.util.Packet
import com.fasterxml.jackson.annotation.JsonSubTypes

/**
 * All messages that are broadcast must implement this interface.
 * The [type] property allows the object to be deserialized correctly.
 *
 * Note that although we are marking [TestMessage] as a subtype here,
 * it is recommended to configure your object mapper to contain additional subtypes.
 * This is necessary for deserialization.
 */
@JsonSubTypes(
    value = [
        JsonSubTypes.Type(TestMessage::class),
        JsonSubTypes.Type(LevelStateBroadcast::class),
    ],
)
interface BroadcastMessage : Packet
