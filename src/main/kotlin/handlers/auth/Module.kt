package me.keraktelor.handlers.auth

import io.ktor.server.routing.*
import me.keraktelor.handlers.auth.handlers.getLoginHandler
import me.keraktelor.handlers.auth.handlers.getRegisterHandler
import me.keraktelor.services.auth.AuthService
import me.keraktelor.utilities.dsl.typedPost
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.ktor.ext.inject

fun Module.authHandler() {
    singleOf(::AuthHandler)
}

fun Routing.authHandler() {
    val handler by inject<AuthHandler>()

    route("/auth") {
        typedPost("/login", handler.getLoginHandler())
        typedPost("/register", handler.getRegisterHandler())
    }
}

class AuthHandler(
    val authService: AuthService,
)
