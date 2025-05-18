package setup

import controllers.initializeRoutes
import io.github.smiley4.ktoropenapi.OpenApi
import io.github.smiley4.ktoropenapi.config.OutputFormat
import io.github.smiley4.ktoropenapi.config.SchemaGenerator
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
        json()
    }
    install(Compression)
    install(OpenApi) {
        outputFormat = OutputFormat.JSON
        schemas {
            generator = SchemaGenerator.kotlinx()
        }
    }
    routing {
        initializeRoutes()
    }
}
