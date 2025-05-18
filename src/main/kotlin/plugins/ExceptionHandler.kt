package plugins

import io.ktor.http.*
import io.ktor.serialization.*
import io.ktor.server.application.*
import io.ktor.server.application.hooks.*
import io.ktor.server.plugins.*
import io.ktor.server.request.ContentTransformationException
import io.ktor.server.response.*
import io.ktor.util.logging.*
import kotlinx.serialization.Serializable

private val logger = KtorSimpleLogger("setup.routing.exceptionHandler")

val ExceptionHandler = createApplicationPlugin(name = "ExceptionHandler") {
    on(CallFailed) { call, err ->
        when (err) {
            is JsonConvertException,
            is BadRequestException,
            is ContentTransformationException,
            -> {
                call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf("message" to "Bad request"),
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

            is UnauthorizedException -> {
                call.respond(
                    HttpStatusCode.Unauthorized,
                    mapOf(
                        "message" to "Unauthorized",
                        "reason" to err.message,
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
