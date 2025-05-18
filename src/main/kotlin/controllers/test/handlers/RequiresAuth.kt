package controllers.test.handlers

import controllers.test.TestController
import io.github.smiley4.ktoropenapi.config.RouteConfig
import io.ktor.http.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import plugins.getAccessTokenInfo
import plugins.ok
import plugins.receiveValidatedQuery

fun RouteConfig.requiresAuthInfo() {
    description = "Test requires auth"
    tags("test")
    request {
        headerParameter<String>("Authorization") {
            example("Token") {
                value = "Bearer <token>"
            }
        }
        queryParameter<String>("message") {
        }
    }
    response {
        code(HttpStatusCode.OK) {
            body<RequiresAuthHandlerResponse>()
        }
        code(HttpStatusCode.Unauthorized) {
        }
    }
}

suspend fun TestController.handleRequiresAuth(
    context: RoutingContext,
) = with(context) {
    val tokenData = call.getAccessTokenInfo()
    val request = call.receiveValidatedQuery<RequiresAuthHandlerRequest>()
    ok {
        RequiresAuthHandlerResponse(
            message = "${tokenData.userId}: '${request.message}'",
        )
    }
}

@Serializable
data class RequiresAuthHandlerRequest(
    val message: String,
)

@Serializable
data class RequiresAuthHandlerResponse(
    val message: String,
)
