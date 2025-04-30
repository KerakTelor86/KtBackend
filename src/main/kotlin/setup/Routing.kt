package me.keraktelor.setup

import io.ktor.server.application.*
import io.ktor.server.routing.*
import me.keraktelor.handlers.HealthHandlers
import me.keraktelor.handlers.TestHandlers
import me.keraktelor.utilities.dsl.typedGet

fun Application.setupRouting() = routing {
    typedGet("/health", HealthHandlers.handleHealth)
    typedGet("/test/{answer}", TestHandlers.handleTest)
}
