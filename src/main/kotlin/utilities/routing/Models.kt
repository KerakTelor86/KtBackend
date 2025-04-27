@file:Suppress("unused")

package me.keraktelor.utilities.routing

import io.ktor.http.*
import kotlinx.serialization.Serializable

@Serializable
object Blank

sealed class Response<TData : Any, TError : Any> {
    object Builder {
        suspend fun <TData : Any, TError : Any> ok(
            body: suspend () -> TData,
        ) = Ok<TData, TError>(
            data = body(),
        )

        suspend fun <TData : Any, TError : Any> badRequest(
            body: suspend () -> TError,
        ) = Error.Client<TData, TError>(
            code = HttpStatusCode.BadRequest,
            error = body()
        )

        suspend fun <TData : Any, TError : Any> unauthorized(
            body: suspend () -> TError,
        ) = Error.Client<TData, TError>(
            code = HttpStatusCode.Unauthorized,
            error = body()
        )

        suspend fun <TData : Any, TError : Any> forbidden(
            body: suspend () -> TError,
        ) = Error.Client<TData, TError>(
            code = HttpStatusCode.Forbidden,
            error = body()
        )

        suspend fun <TData : Any, TError : Any> tooManyRequests(
            body: suspend () -> TError,
        ) = Error.Client<TData, TError>(
            code = HttpStatusCode.TooManyRequests,
            error = body()
        )

        suspend fun <TData : Any, TError : Any> serverError(
            body: suspend () -> Exception,
        ) = Error.Server<TData, TError>(
            code = HttpStatusCode.InternalServerError,
            exception = body(),
        )
    }

    @ConsistentCopyVisibility
    data class Ok<TData : Any, TError : Any> internal constructor(
        val data: TData,
    ) : Response<TData, TError>()

    sealed class Error<TData : Any, TError : Any> : Response<TData, TError>() {
        @ConsistentCopyVisibility
        data class Client<TData : Any, TError : Any> internal constructor(
            val code: HttpStatusCode,
            val error: TError,
        ) : Error<TData, TError>()

        @ConsistentCopyVisibility
        data class Server<TData : Any, TError : Any> internal constructor(
            val code: HttpStatusCode,
            val exception: Exception,
        ) : Error<TData, TError>()
    }
}
