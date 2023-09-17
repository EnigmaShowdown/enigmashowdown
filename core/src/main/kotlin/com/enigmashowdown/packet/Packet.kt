package com.enigmashowdown.packet

import com.fasterxml.jackson.annotation.JsonTypeInfo

// Note we use EXISTING_PROPERTY because we want to ignore the "type" property when constructing/deserializing the JSON object
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
interface Packet {
    val type: String
}
