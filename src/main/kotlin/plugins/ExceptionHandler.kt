package me.keraktelor.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.application.hooks.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.logging.*
import kotlinx.serialization.Serializable

private val logger = KtorSimpleLogger("setup.routing.exceptionHandler")

val ExceptionHandler = createApplicationPlugin(name = "ExceptionHandler") {
    on(CallFailed) { call, err ->
        when (err) {
            is ContentTransformationException -> {
                val message = err.message ?: "Bad request"
                call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf("message" to message),
                )
            }

            is RequestValidationException -> {
                call.respond(
                    HttpStatusCode.BadRequest,
                    RequestValidationExceptionResponse(
                        message = err.message,
                        reasons = err.reasons,
                    ),
                )
            }

            else -> {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf("message" to "Internal server error"),
                )
                logger.error(err)
            }
        }
    }
}

@Serializable
data class RequestValidationExceptionResponse(
    val message: String,
    val reasons: List<String>,
)
