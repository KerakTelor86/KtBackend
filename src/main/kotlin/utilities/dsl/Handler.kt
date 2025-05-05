@file:Suppress("unused")

package me.keraktelor.utilities.dsl

import io.ktor.http.*
import kotlinx.serialization.Serializable

interface Handler<TPath : Any, TIn : Any, TOut : Any, TErr : Exception> {
    companion object Builder {
        fun <TPath : Any, TIn : Any, TOut : Any> createHttpHandler(
            body: suspend (pathParams: TPath, data: TIn) -> Response<TOut>,
        ) = object : Handler<TPath, TIn, TOut, Exception> {
            override suspend fun invoke(
                request: Request<TPath, TIn>,
            ): Response<TOut> = body(request.pathParams, request.data)
        }

        fun <TPath : Any, TIn : Any, TOut : Any, TErr : Exception> Handler<
            TPath,
            TIn,
            TOut,
            Exception,
        >.withExceptionHandler(
            body: suspend (exception: TErr) -> Response<TOut>,
        ) = object : Handler<TPath, TIn, TOut, TErr> {
            override suspend fun invoke(
                request: Request<TPath, TIn>,
            ): Response<TOut> = this@withExceptionHandler(request)

            override suspend fun transformException(
                exception: TErr,
            ): Response<TOut> = body(exception)
        }
    }

    suspend operator fun invoke(request: Request<TPath, TIn>): Response<TOut>

    suspend fun transformException(exception: TErr): Response<TOut> =
        throw exception
}

@Serializable
class Blank

data class Request<TPath, TIn>(
    val pathParams: TPath,
    val data: TIn,
)

data class Response<TData>(
    val statusCode: HttpStatusCode,
    val data: TData,
) {
    companion object Builder {
        fun <T> ok(body: () -> T) = Response(HttpStatusCode.OK, body())

        fun <T> created(body: () -> T) =
            Response(HttpStatusCode.Created, body())

        fun <T> badRequest(body: () -> T) =
            Response(HttpStatusCode.BadRequest, body())

        fun <T> unauthorized(body: () -> T) =
            Response(HttpStatusCode.Unauthorized, body())

        fun <T> forbidden(body: () -> T) =
            Response(HttpStatusCode.Forbidden, body())

        fun <T> conflict(body: () -> T) =
            Response(HttpStatusCode.Conflict, body())

        fun <T> tooManyRequests(body: () -> T) =
            Response(HttpStatusCode.TooManyRequests, body())

        fun <T> internalServerResponse(body: () -> T) =
            Response(HttpStatusCode.InternalServerError, body())

        fun <T> responseWithStatus(
            statusCode: HttpStatusCode,
            body: () -> T,
        ) = Response(statusCode, body())
    }
}

@PublishedApi
internal suspend inline fun <
    TPath : Any,
    TIn : Any,
    TOut : Any,
    reified TErr : Exception,
> Handler<TPath, TIn, TOut, TErr>.handle(
    request: Request<TPath, TIn>,
): Response<TOut> =
    try {
        this(request)
    } catch (e: Exception) {
        if (e is TErr) {
            transformException(e)
        } else {
            throw e
        }
    }
