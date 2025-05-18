package controllers.test

import controllers.test.handlers.handleRequiresAuth
import controllers.test.handlers.requiresAuthInfo
import io.github.smiley4.ktoropenapi.config.RouteConfig
import io.github.smiley4.ktoropenapi.get
import io.ktor.server.routing.*
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.ktor.ext.get
import org.koin.ktor.ext.inject
import plugins.RequiresAuth

fun Module.testController() {
    singleOf(::TestController)
}

fun Routing.testController() {
    val controller by inject<TestController>()

    route("/test") {
        install(RequiresAuth) {
            authService = get()
        }

        get("/needs-auth", RouteConfig::requiresAuthInfo) {
            controller.handleRequiresAuth(this)
        }
    }
}

class TestController
