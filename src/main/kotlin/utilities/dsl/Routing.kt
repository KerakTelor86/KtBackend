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

@JvmName("typedGetNoRoute")
inline fun <
    reified TPath : Any,
    reified TIn : Any,
    reified TOut : Any,
    reified TErr : Exception,
> Routing.typedGet(
    handler: Handler<TPath, TIn, TOut, TErr>,
) = typedGet("", handler)

@JvmName("typedPostNoRoute")
inline fun <
    reified TPath : Any,
    reified TIn : Any,
    reified TOut : Any,
    reified TErr : Exception,
> Routing.typedPost(
    handler: Handler<TPath, TIn, TOut, TErr>,
) = typedPost("", handler)

inline fun <
    reified TPath : Any,
    reified TIn : Any,
    reified TOut : Any,
    reified TErr : Exception,
> Routing.typedGet(
    path: String,
    handler: Handler<TPath, TIn, TOut, TErr>,
) {
    get(path) {
        catchAndLogInternalServerErrors {
            withPathParams<TPath> { pathParams ->
                callHandlerWith(handler) {
                    try {
                        Request(
                            pathParams = pathParams,
                            data = call.queryParameters.toDataClass<TIn>(),
                        )
                    } catch (e: MissingParametersException) {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            MessageAndListResponse(
                                message = e.message,
                                list = e.fieldNames,
                            ),
                        )
                        return@catchAndLogInternalServerErrors
                    }
                }
            }
        }
    }
}

inline fun <
    reified TPath : Any,
    reified TIn : Any,
    reified TOut : Any,
    reified TErr : Exception,
> Routing.typedPost(
    path: String,
    handler: Handler<TPath, TIn, TOut, TErr>,
) {
    post(path) {
        catchAndLogInternalServerErrors {
            withPathParams<TPath> { pathParams ->
                callHandlerWith(handler) {
                    try {
                        Request(
                            pathParams = pathParams,
                            data = call.receive<TIn>(),
                        )
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
    }
}

@PublishedApi
internal inline fun <reified T : Any> Parameters.toDataClass(): T {
    val kClass = T::class

    val constructor = kClass.primaryConstructor
        ?: throw UnsupportedTInException(
            kClass.qualifiedName.orEmpty(),
        )

    val missingParameters = mutableListOf<String>()
    val parameters = constructor.parameters.associateWith {
        val key = it.name.orEmpty()
        val value = this[key].orEmpty()
        try {
            when (it.type) {
                typeOf<String>() -> value
                typeOf<Int>() -> value.toInt()
                typeOf<Long>() -> value.toLong()
                typeOf<Float>() -> value.toFloat()
                typeOf<Double>() -> value.toDouble()
                else ->
                    throw UnsupportedTInException(
                        kClass.qualifiedName.orEmpty(),
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
        throw MissingParametersException(missingParameters)
    }

    return constructor.callBy(parameters)
}

@PublishedApi
internal suspend inline fun <
    reified TPath : Any,
> RoutingContext.withPathParams(
    body: RoutingContext.(TPath) -> Unit,
) {
    val pathParams =
        try {
            call.pathParameters.toDataClass<TPath>()
        } catch (e: MissingParametersException) {
            return call.respond(
                HttpStatusCode.BadRequest,
                MessageAndListResponse(
                    message = e.message,
                    list = e.fieldNames,
                ),
            )
        }

    body(pathParams)
}

@PublishedApi
internal suspend inline fun <
    reified TPath : Any,
    reified TIn : Any,
    reified TOut : Any,
    reified TErr : Exception,
> RoutingContext.callHandlerWith(
    handler: Handler<TPath, TIn, TOut, TErr>,
    body: RoutingContext.() -> Request<TPath, TIn>,
) {
    val request = body()

    if (request.pathParams is Validatable) {
        val validationResult = request.pathParams.validate()
        if (validationResult is ValidationResult.Invalid) {
            return call.respond(
                HttpStatusCode.BadRequest,
                MessageAndListResponse(
                    message = "Path parameter validation failed",
                    list = validationResult.reasons,
                ),
            )
        }
    }

    if (request.data is Validatable) {
        val validationResult = request.data.validate()
        if (validationResult is ValidationResult.Invalid) {
            return call.respond(
                HttpStatusCode.BadRequest,
                MessageAndListResponse(
                    message = "Request data validation failed",
                    list = validationResult.reasons,
                ),
            )
        }
    }

    val response = handler.handle(request)
    call.respond(response.statusCode, response.data)
}

class UnsupportedTInException(
    private val className: String,
) : Exception() {
    override val message
        get() = "GET receive not supported for '$className'" +
            "-- Hint: Use POST instead"
}

class MissingParametersException(
    val fieldNames: List<String>,
) : Exception() {
    override val message
        get() = "Missing parameters"
}

private val exceptionLogger = KtorSimpleLogger("dsl.routing.uncaughtException")

@PublishedApi
@Serializable
internal data class MessageResponse(
    val message: String,
)

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
