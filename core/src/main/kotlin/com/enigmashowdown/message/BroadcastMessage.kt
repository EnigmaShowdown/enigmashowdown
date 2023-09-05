package com.enigmashowdown.message

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

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
    ],
)
// Note we use EXISTING_PROPERTY because we want to ignore the "type" property when constructing/deserializing the JSON object
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
interface BroadcastMessage {
    val type: String
}
