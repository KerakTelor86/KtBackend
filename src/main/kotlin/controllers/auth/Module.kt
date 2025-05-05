package me.keraktelor.controllers.auth

import io.ktor.server.routing.*
import me.keraktelor.controllers.auth.handlers.getLoginHandler
import me.keraktelor.controllers.auth.handlers.getRefreshHandler
import me.keraktelor.controllers.auth.handlers.getRegisterHandler
import me.keraktelor.services.auth.AuthService
import me.keraktelor.utilities.dsl.typedPost
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.ktor.ext.inject

fun Module.authController() {
    singleOf(::AuthController)
}

fun Routing.authController() {
    val controller by inject<AuthController>()

    route("/auth") {
        typedPost("/register", controller.getRegisterHandler())
        typedPost("/refresh", controller.getRefreshHandler())
        typedPost("/login", controller.getLoginHandler())
    }
}

class AuthController(
    val authService: AuthService,
)
