package me.keraktelor.handlers

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import me.keraktelor.utilities.routing.Blank
import me.keraktelor.utilities.routing.Handler
import me.keraktelor.utilities.routing.Response.Builder.ok

object HealthHandlers {
    val handleHealth: Handler<Blank, HealthResponse, Blank> = { _ ->
        ok {
            HealthResponse(Clock.System.now().toLocalDateTime(systemTimeZone))
        }
    }
}

private val systemTimeZone = TimeZone.currentSystemDefault()

@Serializable
data class HealthResponse(val serverTime: LocalDateTime)
