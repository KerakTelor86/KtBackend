package controllers.auth

import controllers.auth.handlers.*
import io.github.smiley4.ktoropenapi.config.RouteConfig
import io.github.smiley4.ktoropenapi.post
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
        post("/register", RouteConfig::registerInfo) {
            controller.handleRegister(this)
        }
        post("/refresh", RouteConfig::refreshInfo) {
            controller.handleRefresh(this)
        }
        post("/login", RouteConfig::loginInfo) {
            controller.handleLogin(this)
        }
    }
}

class AuthController(
    val authService: AuthService,
)
