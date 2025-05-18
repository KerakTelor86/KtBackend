@file:Suppress("unused")

package utilities.routing

import io.github.smiley4.ktoropenapi.config.RouteConfig
import io.github.smiley4.ktoropenapi.get
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import plugins.receiveValidatedBody
import plugins.receiveValidatedHeaders
import plugins.receiveValidatedPath
import plugins.receiveValidatedQuery
import utilities.routing.HandlerBuilder.Companion.build
import utilities.routing.HandlerBuilder.Companion.document
import kotlin.reflect.typeOf

@DslMarker
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.TYPE,
)
annotation class RoutingDsl

fun Routing.documentRoutes(body: DocumentedRoute.() -> Unit) {
    DocumentedRoute(this).apply(body)
}

@Serializable
class Blank

val buildHandler = HandlerBuilder<Blank, Blank, Blank, Blank>().handle {}

@RoutingDsl
class DocumentedRoute(
    val routing: Routing,
) {
    inline fun <
        reified THeader : Any,
        reified TPath : Any,
        reified TQuery : Any,
        reified TBody : Any,
    > get(
        path: String,
        builder: HandlerBuilder<THeader, TPath, TQuery, TBody>,
    ) {
        routing.get(path, { builder.document(this) }, builder.build())
    }
}

@RoutingDsl
class HandlerBuilder<THeader : Any, TPath : Any, TQuery : Any, TBody : Any> {
    fun <T : Any> withHeaderParams() =
        HandlerBuilder<T, TPath, TQuery, TBody>()

    fun <T : Any> withPathParams() =
        HandlerBuilder<THeader, T, TQuery, TBody>()

    fun <T : Any> withQueryParams() =
        HandlerBuilder<THeader, TPath, T, TBody>()

    fun <T : Any> withBody() =
        HandlerBuilder<THeader, TPath, TQuery, T>()

    var handler: Handler<THeader, TPath, TQuery, TBody>? = null

    fun handle(
        handler: suspend RoutingContext.(
            params: HandlerParams<THeader, TPath, TQuery, TBody>,
        ) -> Unit,
    ) = apply {
        if (this.handler != null) {
            throw IllegalStateException(
                "Handler is already defined for this builder",
            )
        }
        this.handler = handler
    }

    @PublishedApi
    internal companion object {
        @Suppress("UnusedReceiverParameter")
        inline fun <
            reified THeader : Any,
            reified TPath : Any,
            reified TQuery : Any,
            reified TBody : Any,
        > HandlerBuilder<
            THeader,
            TPath,
            TQuery,
            TBody,
        >.document(routeConfig: RouteConfig) =
            routeConfig.apply {
                request {
                    if (typeOf<THeader>() != typeOf<Blank>()) {
                        val params = getConstructorParams<THeader>()
                        params.forEach { (name, type) ->
                            headerParameter(name, type)
                        }
                    }
                    if (typeOf<TPath>() != typeOf<Blank>()) {
                        val params = getConstructorParams<TPath>()
                        params.forEach { (name, type) ->
                            pathParameter(name, type)
                        }
                    }
                    if (typeOf<TQuery>() != typeOf<Blank>()) {
                        val params = getConstructorParams<TQuery>()
                        params.forEach { (name, type) ->
                            queryParameter(name, type)
                        }
                    }
                    if (typeOf<TBody>() != typeOf<Blank>()) {
                        body<TBody>()
                    }
                }
            }

        inline fun <
            reified THeader : Any,
            reified TPath : Any,
            reified TQuery : Any,
            reified TBody : Any,
        > HandlerBuilder<
            THeader,
            TPath,
            TQuery,
            TBody,
        >.build(): suspend RoutingContext.() -> Unit {
            val handler = handler
                ?: throw IllegalStateException("Handler not yet defined")

            return {
                val params = HandlerParams<
                    THeader,
                    TPath,
                    TQuery,
                    TBody,
                >(
                    headerParams = call.receiveValidatedHeaders(),
                    pathParams = call.receiveValidatedPath(),
                    queryParams = call.receiveValidatedQuery(),
                    body = call.receiveValidatedBody(),
                )

                handler(params)
            }
        }
    }
}

typealias Handler<THeader, TPath, TQuery, TBody> = suspend RoutingContext.(
    params: HandlerParams<THeader, TPath, TQuery, TBody>,
) -> Unit

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
