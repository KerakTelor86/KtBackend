package controllers.auth

import controllers.auth.handlers.handleLogin
import controllers.auth.handlers.handleRefresh
import controllers.auth.handlers.handleRegister
import io.ktor.server.routing.*
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.ktor.ext.inject
import services.auth.AuthService

fun Module.authController() {
    singleOf(::AuthController)
}

fun Routing.authController() {
    val controller by inject<AuthController>()

    route("/auth") {
        post("/register") { controller.handleRegister(this) }
        post("/refresh") { controller.handleRefresh(this) }
        post("/login") { controller.handleLogin(this) }
    }
}

class AuthController(
    val authService: AuthService,
)
