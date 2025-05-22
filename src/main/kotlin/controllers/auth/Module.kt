package controllers.auth

import controllers.auth.handlers.buildLoginHandler
import controllers.auth.handlers.buildRefreshHandler
import controllers.auth.handlers.buildRegisterHandler
import io.ktor.server.routing.*
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.ktor.ext.inject
import services.auth.AuthService
import utilities.routing.documentRoutes

fun Module.authController() {
    singleOf(::AuthController)
}

fun Routing.authController() {
    val controller by inject<AuthController>()

    route("/auth") {
        documentRoutes {
            post("/login", controller.buildLoginHandler())
            post("/refresh", controller.buildRefreshHandler())
            post("/register", controller.buildRegisterHandler())
        }
    }
}

class AuthController(
    val authService: AuthService,
)
