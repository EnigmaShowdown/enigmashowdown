package com.enigmashowdown.message.request

import com.fasterxml.jackson.annotation.JsonTypeName
import java.util.UUID

/**
 * Requests a change to the current level being played.
 */
@JsonTypeName("level-request")
data class LevelRequest(
    val levelId: UUID,
) : RequestMessage {
    override val type: String
        get() = "level-request"
}
