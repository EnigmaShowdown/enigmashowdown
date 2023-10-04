package com.enigmashowdown

import java.time.Duration

object EnigmaShowdownConstants {
    const val TICKS_PER_SECOND = 10
    const val TICK_PERIOD_MILLIS = 100L
    val TICK_PERIOD = Duration.ofMillis(TICK_PERIOD_MILLIS)
    const val TICK_PERIOD_SECONDS = 0.1f
    const val PORT_SERVER = 31877
    const val PORT_BROADCAST = 31878

    /** ZeroMQ has the ability for a SUB connection to subscribe to a particular topic. We don't support this feature yet, so this constant should be used wherever we have the option to pass a subscribe topic*/
    const val SUBSCRIBE_TOPIC_ALL = ""
}
