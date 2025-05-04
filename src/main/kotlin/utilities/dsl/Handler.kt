@file:Suppress("unused")

package me.keraktelor.utilities.dsl

import io.ktor.http.*
import kotlinx.serialization.Serializable

interface Handler<TPath : Any, TIn : Any, TOut : Any, TErr : Exception> {
    companion object Builder {
        fun <TPath : Any, TIn : Any, TOut : Any> createHttpHandler(
            body: suspend (pathParams: TPath, data: TIn) -> TOut,
        ) = object : Handler<TPath, TIn, TOut, Exception> {
            override suspend fun invoke(request: Request<TPath, TIn>): TOut =
                body(request.pathParams, request.data)
        }

        fun <TPath : Any, TIn : Any, TOut : Any, TErr : Exception> Handler<
            TPath,
            TIn,
            TOut,
            Exception,
        >.withExceptionHandler(
            body: suspend (exception: TErr) -> Response<Any>,
        ) = object : Handler<TPath, TIn, TOut, TErr> {
            override suspend fun invoke(request: Request<TPath, TIn>): TOut =
                this@withExceptionHandler(request)

            override suspend fun transformException(
                exception: TErr,
            ): Response<Any> = body(exception)
        }
    }

    suspend operator fun invoke(request: Request<TPath, TIn>): TOut

    suspend fun transformException(exception: TErr): Response<Any> =
        throw exception
}

@Serializable
class Blank

data class Request<TPath, TIn>(
    val pathParams: TPath,
    val data: TIn,
)

sealed class Response<TData> {
    abstract val statusCode: HttpStatusCode
    abstract val data: TData

    data class Ok<TData>(
        override val data: TData,
    ) : Response<TData>() {
        override val statusCode
            get() = HttpStatusCode.OK
    }

    data class Error<TData>(
        override val statusCode: HttpStatusCode,
        override val data: TData,
    ) : Response<TData>()
}

@PublishedApi
internal suspend inline fun <
    TPath : Any,
    TIn : Any,
    TOut : Any,
    reified TErr : Exception,
> Handler<TPath, TIn, TOut, TErr>.handle(
    request: Request<TPath, TIn>,
): Response<Any> =
    try {
        Response.Ok(this(request))
    } catch (e: Exception) {
        if (e is TErr) {
            transformException(e)
        } else {
            throw e
        }
    }
