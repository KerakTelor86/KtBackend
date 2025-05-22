@file:Suppress("unused")

package utilities.routing

import io.github.smiley4.ktoropenapi.*
import io.ktor.server.routing.*

@DslMarker
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.TYPE,
)
annotation class RoutingDsl

fun Route.documentRoutes(body: DocumentedRoute.() -> Unit) {
    DocumentedRoute(this).apply(body)
}

class Blank

val buildHandler = HandlerBuilder<
    Blank,
    Blank,
    Blank,
    Blank,
    Blank,
>().handle<Blank> {
    throw IllegalStateException(
        "buildHandler called without defining handler",
    )
}

@RoutingDsl
class DocumentedRoute(
    val routing: Route,
) {
    inline fun <
        reified THeader : Any,
        reified TPath : Any,
        reified TQuery : Any,
        reified TBody : Any,
        reified TResponse : Any,
    > get(
        path: String,
        builder: HandlerBuilder<THeader, TPath, TQuery, TBody, TResponse>,
    ) {
        routing.get(path, { builder.document(this) }, builder.build())
    }

    inline fun <
        reified THeader : Any,
        reified TPath : Any,
        reified TQuery : Any,
        reified TBody : Any,
        reified TResponse : Any,
    > post(
        path: String,
        builder: HandlerBuilder<THeader, TPath, TQuery, TBody, TResponse>,
    ) {
        routing.post(path, { builder.document(this) }, builder.build())
    }

    inline fun <
        reified THeader : Any,
        reified TPath : Any,
        reified TQuery : Any,
        reified TBody : Any,
        reified TResponse : Any,
    > put(
        path: String,
        builder: HandlerBuilder<THeader, TPath, TQuery, TBody, TResponse>,
    ) {
        routing.put(path, { builder.document(this) }, builder.build())
    }

    inline fun <
        reified THeader : Any,
        reified TPath : Any,
        reified TQuery : Any,
        reified TBody : Any,
        reified TResponse : Any,
    > patch(
        path: String,
        builder: HandlerBuilder<THeader, TPath, TQuery, TBody, TResponse>,
    ) {
        routing.patch(path, { builder.document(this) }, builder.build())
    }

    inline fun <
        reified THeader : Any,
        reified TPath : Any,
        reified TQuery : Any,
        reified TBody : Any,
        reified TResponse : Any,
    > delete(
        path: String,
        builder: HandlerBuilder<THeader, TPath, TQuery, TBody, TResponse>,
    ) {
        routing.delete(path, { builder.document(this) }, builder.build())
    }

    inline fun <
        reified THeader : Any,
        reified TPath : Any,
        reified TQuery : Any,
        reified TBody : Any,
        reified TResponse : Any,
    > options(
        path: String,
        builder: HandlerBuilder<THeader, TPath, TQuery, TBody, TResponse>,
    ) {
        routing.options(path, { builder.document(this) }, builder.build())
    }

    inline fun <
        reified THeader : Any,
        reified TPath : Any,
        reified TQuery : Any,
        reified TBody : Any,
        reified TResponse : Any,
    > head(
        path: String,
        builder: HandlerBuilder<THeader, TPath, TQuery, TBody, TResponse>,
    ) {
        routing.head(path, { builder.document(this) }, builder.build())
    }
}

@RoutingDsl
class HandlerBuilder<
    THeader : Any,
    TPath : Any,
    TQuery : Any,
    TBody : Any,
    TResponse : Any,
> {
    fun <T : Any> withHeaderParams() =
        HandlerBuilder<T, TPath, TQuery, TBody, TResponse>()

    fun <T : Any> withPathParams() =
        HandlerBuilder<THeader, T, TQuery, TBody, TResponse>()

    fun <T : Any> withQueryParams() =
        HandlerBuilder<THeader, TPath, T, TBody, TResponse>()

    fun <T : Any> withBody() =
        HandlerBuilder<THeader, TPath, TQuery, T, TResponse>()

    @PublishedApi
    internal var handler: Handler<THeader, TPath, TQuery, TBody, TResponse>? =
        null

    fun <R : Any> handle(
        handler: suspend RoutingContext.(
            params: HandlerParams<THeader, TPath, TQuery, TBody>,
        ) -> R,
    ) = HandlerBuilder<
        THeader,
        TPath,
        TQuery,
        TBody,
        R,
    >().apply {
        if (this.handler != null) {
            throw IllegalStateException(
                "Handler is already defined for this builder",
            )
        }
        this.handler = handler
    }
}

typealias Handler<
    THeader,
    TPath,
    TQuery,
    TBody,
    TResponse,
> = suspend RoutingContext.(
    params: HandlerParams<THeader, TPath, TQuery, TBody>,
) -> TResponse

data class HandlerParams<
    THeader : Any,
    TPath : Any,
    TQuery : Any,
    TBody : Any,
>(
    val headerParams: THeader,
    val pathParams: TPath,
    val queryParams: TQuery,
    val body: TBody,
)
