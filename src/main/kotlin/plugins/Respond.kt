@file:Suppress("unused")

package plugins

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

suspend inline fun <reified T : Any> RoutingContext.ok(
    body: () -> T,
) = call.respond(
    status = HttpStatusCode.OK,
    body(),
)

suspend inline fun <reified T : Any> RoutingContext.created(
    body: () -> T,
) = call.respond(
    status = HttpStatusCode.Created,
    body(),
)

suspend inline fun <reified T : Any> RoutingContext.badRequest(
    body: () -> T,
) = call.respond(
    status = HttpStatusCode.BadRequest,
    body(),
)

suspend inline fun <reified T : Any> RoutingContext.unauthorized(
    body: () -> T,
) = call.respond(
    status = HttpStatusCode.Unauthorized,
    body(),
)

suspend inline fun <reified T : Any> RoutingContext.forbidden(
    body: () -> T,
) = call.respond(
    status = HttpStatusCode.Forbidden,
    body(),
)

suspend inline fun <reified T : Any> RoutingContext.conflict(
    body: () -> T,
) = call.respond(
    status = HttpStatusCode.Conflict,
    body(),
)

suspend inline fun <reified T : Any> RoutingContext.tooManyRequests(
    body: () -> T,
) = call.respond(
    status = HttpStatusCode.TooManyRequests,
    body(),
)

suspend inline fun <reified T : Any> RoutingContext.internalServerError(
    body: () -> T,
) = call.respond(
    status = HttpStatusCode.InternalServerError,
    body(),
)
