@file:Suppress("unused")

package me.keraktelor.utilities.dsl

import io.ktor.http.*
import kotlinx.serialization.Serializable

interface Handler<TIn : Any, TOut : Any, TErr : Exception> {
    companion object Builder {
        fun <TIn : Any, TOut : Any> createHttpHandler(
            body: suspend (TIn) -> TOut,
        ): Handler<TIn, TOut, Exception> {
            return object : Handler<TIn, TOut, Exception> {
                override suspend fun invoke(request: TIn): TOut {
                    return body(request)
                }
            }
        }

        fun <
                TIn : Any,
                TOut : Any,
                TErr : Exception,
                > Handler<TIn, TOut, Exception>.withExceptionHandler(
            body: suspend (TErr) -> Response<Any>,
        ): Handler<TIn, TOut, TErr> {
            return object : Handler<TIn, TOut, TErr> {
                override suspend fun invoke(
                    request: TIn,
                ): TOut {
                    return this@withExceptionHandler(request)
                }

                override suspend fun transformException(
                    exception: TErr,
                ): Response<Any> {
                    return body(exception)
                }
            }
        }
    }

    suspend operator fun invoke(request: TIn): TOut

    suspend fun transformException(exception: TErr): Response<Any> {
        throw exception
    }
}

@Serializable
class Blank

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
        TIn : Any,
        TOut : Any,
        reified TErr : Exception,
        > Handler<TIn, TOut, TErr>.handle(
    request: TIn,
): Response<Any> {
    return try {
        Response.Ok(this(request))
    } catch (e: Exception) {
        if (e is TErr) {
            transformException(e)
        } else {
            throw e
        }
    }
}
