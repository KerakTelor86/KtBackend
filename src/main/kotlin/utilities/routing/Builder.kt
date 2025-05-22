package utilities.routing

import io.github.smiley4.ktoropenapi.config.ResponsesConfig
import io.github.smiley4.ktoropenapi.config.RouteConfig
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import plugins.*
import kotlin.reflect.KClass
import kotlin.reflect.full.createType
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.typeOf

@Suppress("UnusedReceiverParameter")
@PublishedApi
internal inline fun <
    reified THeader : Any,
    reified TPath : Any,
    reified TQuery : Any,
    reified TBody : Any,
    reified TResponse : Any,
> HandlerBuilder<
    THeader,
    TPath,
    TQuery,
    TBody,
    TResponse,
>.document(routeConfig: RouteConfig) = routeConfig.apply {
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
    response {
        if (typeOf<TResponse>() != typeOf<Blank>()) {
            document(TResponse::class)
        }

        val requiresValidation = listOf(
            THeader::class,
            TPath::class,
            TQuery::class,
            TBody::class,
        ).any {
            it.isSubclassOf(RequiresValidation::class)
        }
        if (requiresValidation) {
            code(HttpStatusCode.BadRequest) {
                body<RequestValidationExceptionResponse> {
                    example("Request does not pass validation") {
                        value = RequestValidationExceptionResponse(
                            message = "Request does not pass validation",
                            reasons = listOf(
                                "Reason #1",
                                "Reason #2",
                                "Reason ..",
                                "Reason #N",
                            ),
                        )
                    }
                }
            }
        }
    }
}

@PublishedApi
internal inline fun <
    reified THeader : Any,
    reified TPath : Any,
    reified TQuery : Any,
    reified TBody : Any,
    reified TResponse : Any,
> HandlerBuilder<
    THeader,
    TPath,
    TQuery,
    TBody,
    TResponse,
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

        apply {
            val result = handler(params)
            call.respond(
                status = getStatusCode(result::class),
                message = result,
            )
        }
    }
}

@PublishedApi
internal fun <T : Any> ResponsesConfig.document(
    kClass: KClass<T>,
) {
    if (kClass.isSealed) {
        kClass.nestedClasses.forEach { document(it) }
        return
    }

    if (!kClass.isData) {
        return
    }

    code(getStatusCode(kClass)) {
        body(kClass.createType())
    }
}
