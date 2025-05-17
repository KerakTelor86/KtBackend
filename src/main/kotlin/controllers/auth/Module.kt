package me.keraktelor.controllers.auth

import io.ktor.server.routing.*
import me.keraktelor.controllers.auth.handlers.handleLogin
import me.keraktelor.controllers.auth.handlers.handleRefresh
import me.keraktelor.controllers.auth.handlers.handleRegister
import me.keraktelor.services.auth.AuthService
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.ktor.ext.inject

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
