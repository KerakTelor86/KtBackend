package me.keraktelor.setup

import io.ktor.server.application.*
import io.ktor.server.routing.*
import me.keraktelor.controllers.auth.authController
import me.keraktelor.utilities.dsl.Blank
import me.keraktelor.utilities.dsl.Handler.Builder.createHttpHandler
import me.keraktelor.utilities.dsl.Response.Builder.ok
import me.keraktelor.utilities.dsl.typedGet
import java.time.OffsetDateTime

fun Application.setupRouting() {
    install(IgnoreTrailingSlash)

    routing {
        defaultRoutes()

        authController()
    }
}

fun Routing.defaultRoutes() {
    val handler = createHttpHandler { _: Blank, _: Blank ->
        ok {
            mapOf(
                "serverTime" to OffsetDateTime.now().toString(),
            )
        }
    }

    typedGet("/", handler)
}
