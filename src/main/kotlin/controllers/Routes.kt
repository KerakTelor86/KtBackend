package me.keraktelor.controllers

import io.ktor.server.routing.*
import me.keraktelor.controllers.auth.authController
import me.keraktelor.plugins.ok
import java.time.OffsetDateTime

fun Routing.initializeRoutes() {
    defaultRoutes()

    authController()
}

fun Routing.defaultRoutes() {
    get("/") {
        ok {
            mapOf(
                "serverTime" to OffsetDateTime.now().toString(),
            )
        }
    }
}
