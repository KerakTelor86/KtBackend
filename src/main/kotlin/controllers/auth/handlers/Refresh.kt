package controllers.auth.handlers

import controllers.auth.AuthController
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import plugins.ok
import plugins.receiveValidatedBody
import services.auth.RefreshServiceReq
import services.auth.RefreshServiceRes

suspend fun AuthController.handleRefresh(
    context: RoutingContext,
) = with(context) {
    val request = call.receiveValidatedBody<RefreshHandlerRequest>()

    val result = authService.refresh(
        RefreshServiceReq(refreshToken = request.refreshToken),
    )

    when (result) {
        is RefreshServiceRes.Ok -> ok {
            RefreshHandlerResponse.Ok(
                accessToken = result.accessToken,
            )
        }
    }
}

@Serializable
data class RefreshHandlerRequest(
    val refreshToken: String,
)

@Serializable
sealed class RefreshHandlerResponse {
    @Serializable
    data class Ok(
        val accessToken: String,
    ) : RefreshHandlerResponse()
}
