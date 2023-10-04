package com.enigmashowdown.util

import com.enigmashowdown.EnigmaShowdownConstants
import java.time.Duration

fun Duration.toTicks(): Int {
    return (toMillis() * EnigmaShowdownConstants.TICKS_PER_SECOND / 1000).toInt()
}
