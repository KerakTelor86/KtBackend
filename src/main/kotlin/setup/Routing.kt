package me.keraktelor.setup

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.compression.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import me.keraktelor.controllers.initializeRoutes
import me.keraktelor.plugins.ExceptionHandler

fun Application.setupRouting() {
    install(IgnoreTrailingSlash)
    install(ExceptionHandler)
    install(ContentNegotiation) {
        json()
    }
    install(Compression)
    routing {
        initializeRoutes()
    }
}
