package controllers

import controllers.auth.authController
import io.ktor.server.routing.*
import plugins.ok
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
