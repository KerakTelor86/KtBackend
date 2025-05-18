package controllers

import controllers.auth.authController
import io.github.smiley4.ktoropenapi.get
import io.github.smiley4.ktoropenapi.openApi
import io.github.smiley4.ktorredoc.redoc
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Routing.initializeRoutes() {
    defaultRoutes()

    authController()
}

fun Routing.defaultRoutes() {
    route("/api.json") {
        openApi()
    }
    route("/redoc") {
        redoc("/api.json")
    }
    get("/", {
        hidden = true
    }) {
        call.respondRedirect("/redoc")
    }
}
