package setup

import controllers.initializeRoutes
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.compression.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import plugins.ExceptionHandler

fun Application.setupRouting() {
    install(IgnoreTrailingSlash)
    install(ExceptionHandler)
    install(ContentNegotiation) {
        json(
//            json = Json {
//                isLenient = true
//                ignoreUnknownKeys = true
//            },
        )
    }
    install(Compression)
    routing {
        initializeRoutes()
    }
}
