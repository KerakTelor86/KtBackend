@file:Suppress("unused")

package me.keraktelor.utilities.dsl

import io.ktor.http.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.logging.*
import kotlinx.serialization.Serializable
import me.keraktelor.utilities.validation.Validatable
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.typeOf

inline fun <
        reified TIn : Any,
        reified TOut : Any,
        reified TErr : Exception,
        > Routing.typedGet(
    path: String,
    handler: Handler<TIn, TOut, TErr>,
) = get(path) {
    catchAndLogInternalServerErrors {
        callHandlerWith(handler) {
            try {
                val kClass = TIn::class

                val constructor = kClass.primaryConstructor
                    ?: throw UnsupportedTInException(kClass.qualifiedName.orEmpty())

                val missingParameters = mutableListOf<String>()
                val parameters = constructor.parameters.associateWith {
                    val key = it.name.orEmpty()
                    val value = call.parameters[key].orEmpty()
                    try {
                        when (it.type) {
                            typeOf<String>() -> value
                            typeOf<Int>() -> value.toInt()
                            typeOf<Long>() -> value.toLong()
                            typeOf<Float>() -> value.toFloat()
                            typeOf<Double>() -> value.toDouble()
                            else -> throw UnsupportedTInException(
                                kClass.qualifiedName.orEmpty()
                            )
                        }
                    } catch (_: NumberFormatException) {
                        if (!it.type.isMarkedNullable) {
                            missingParameters.add(key)
                        }
                        null
                    }
                }

                if (missingParameters.isNotEmpty()) {
                    throw MissingQueryParametersException(missingParameters)
                }

                constructor.callBy(parameters)
            } catch (e: MissingQueryParametersException) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    MessageAndListResponse(
                        message = e.message,
                        list = e.fieldNames
                    ),
                )
                return@catchAndLogInternalServerErrors
            }
        }
    }
}

inline fun <
        reified TIn : Any,
        reified TOut : Any,
        reified TErr : Exception,
        > Routing.typedPost(
    path: String,
    handler: Handler<TIn, TOut, TErr>,
) = post(path) {
    catchAndLogInternalServerErrors {
        callHandlerWith(handler) {
            try {
                call.receive<TIn>()
            } catch (e: ContentTransformationException) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    MessageResponse(e.message.orEmpty()),
                )
                return@catchAndLogInternalServerErrors
            }
        }
    }
}

@PublishedApi
internal suspend inline fun <
        reified TIn : Any,
        reified TOut : Any,
        reified TErr : Exception,
        > RoutingContext.callHandlerWith(
    handler: Handler<TIn, TOut, TErr>,
    body: RoutingContext.() -> TIn,
) {
    val request = body()

    if (request is Validatable) {
        val validationResult = request.validate()
        if (validationResult is ValidationResult.Invalid) {
            call.respond(
                HttpStatusCode.BadRequest,
                MessageAndListResponse(
                    message = "Validation failed",
                    list = validationResult.reasons,
                )
            )
        }
    }

    val response = handler.handle(request)
    call.respond(
        response.statusCode,
        response.data,
    )
}

class UnsupportedTInException(private val className: String) : Exception() {
    override val message
        get() =
            "GET receive not supported for '$className'" +
                    "-- Hint: Use POST instead"
}

class MissingQueryParametersException(
    val fieldNames: List<String>,
) : Exception() {
    override val message get() = "Missing query parameters"
}

private val exceptionLogger = KtorSimpleLogger("dsl.routing.uncaughtException")

@PublishedApi
@Serializable
internal data class MessageResponse(val message: String)

@PublishedApi
@Serializable
internal data class MessageAndListResponse<T>(
    val message: String,
    val list: List<T>,
)

@PublishedApi
internal suspend fun RoutingContext.catchAndLogInternalServerErrors(
    body: suspend RoutingContext.() -> Unit,
) {
    try {
        body()
    } catch (e: Exception) {
        call.respond(
            HttpStatusCode.InternalServerError,
            MessageResponse("Internal server error"),
        )
        exceptionLogger.error(e)
    }
}
