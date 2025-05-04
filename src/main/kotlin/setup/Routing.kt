package me.keraktelor.setup

import io.ktor.server.application.*
import io.ktor.server.routing.*
import me.keraktelor.handlers.auth.authHandler

fun Application.setupRouting() {
    routing {
        authHandler()
    }
}
