@file:Suppress("unused")

package me.keraktelor.utilities.routing

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.logging.*
import me.keraktelor.utilities.routing.Response.Builder.serverError

typealias Handler<TRequest, TResponse, TError> =
        suspend RoutingContext.(TRequest) -> Response<TResponse, TError>

@PublishedApi
internal val logger = KtorSimpleLogger("utilities.routing.dsl")

@JvmName("typedGetBlankReq")
inline fun <reified TResponse : Any, reified TError : Any> Routing.typedGet(
    path: String,
    crossinline handler: Handler<Blank, TResponse, TError>,
) = typedGet<Blank, TResponse, TError>(path, handler)

inline fun <reified TRequest : Any, reified TResponse : Any, reified TError : Any> Routing.typedGet(
    path: String,
    crossinline handler: Handler<TRequest, TResponse, TError>,
) {
    get(path) {
        callHandler(handler) {
            try {
                call.parameters.toDataClass<TRequest>()
            } catch (e: MissingParameterException) {
                call.response.status(HttpStatusCode.BadRequest)
                call.respond(mapOf("message" to e.message.orEmpty()))
                throw NoResponseNeededException()
            }
        }
    }
}

inline fun <reified TRequest : Any, reified TResponse : Any, reified TError : Any> Routing.typedPost(
    path: String,
    crossinline handler: Handler<TRequest, TResponse, TError>,
) {
    post(path) {
        callHandler(handler) {
            try {
                call.receive<TRequest>()
            } catch (e: ContentTransformationException) {
                call.response.status(HttpStatusCode.BadRequest)
                call.respond(mapOf("message" to e.message.orEmpty()))
                throw NoResponseNeededException()
            }
        }
    }
}

@PublishedApi
internal suspend inline fun <reified TRequest : Any, reified TResponse : Any, reified TError : Any> RoutingContext.callHandler(
    crossinline handler: Handler<TRequest, TResponse, TError>,
    crossinline getRequest: suspend RoutingContext.() -> TRequest,
) {
    val response = try {
        val request = try {
            getRequest()
        } catch (e: NoResponseNeededException) {
            return
        }

        handler(request)
    } catch (e: Exception) {
        serverError { e }
    }

    when (response) {
        is Response.Ok<*, *> -> {
            call.response.status(HttpStatusCode.OK)
            call.respond(response.data)
        }

        is Response.Error.Client<*, *> -> {
            call.response.status(response.code)
            call.respond(response.error)
        }

        is Response.Error.Server<*, *> -> {
            call.response.status(response.code)
            call.respond(mapOf("message" to "Internal server error"))

            logger.error(response.exception)
        }
    }
}
