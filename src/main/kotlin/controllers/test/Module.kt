package controllers.test

import controllers.test.handlers.buildRequiresAuthHandler
import io.ktor.server.routing.*
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.ktor.ext.get
import org.koin.ktor.ext.inject
import plugins.RequiresAuth
import utilities.routing.documentRoutes

fun Module.testController() {
    singleOf(::TestController)
}

fun Routing.testController() {
    val controller by inject<TestController>()

    route("/test") {
        install(RequiresAuth) {
            authService = get()
        }

        documentRoutes {
            get("/needs-auth", controller.buildRequiresAuthHandler())
        }
    }
}

class TestController {
    val testPrefix = "test"
}
