package me.keraktelor.handlers

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import me.keraktelor.utilities.dsl.Blank
import me.keraktelor.utilities.dsl.Handler.Builder.createHttpHandler

object HealthHandlers {
    val handleHealth = createHttpHandler { _: Blank ->
        HealthResponse(
            Clock.System.now().toLocalDateTime(systemTimeZone)
        )
    }
}

private val systemTimeZone = TimeZone.currentSystemDefault()

@Serializable
data class HealthResponse(val serverTime: LocalDateTime)