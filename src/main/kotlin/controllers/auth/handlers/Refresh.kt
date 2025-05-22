package controllers.auth.handlers

import controllers.auth.AuthController
import kotlinx.serialization.Serializable
import services.auth.RefreshServiceReq
import services.auth.RefreshServiceRes
import utilities.routing.StatusCode
import utilities.routing.buildHandler

fun AuthController.buildRefreshHandler() = buildHandler
    .withBody<RefreshHandlerRequest>()
    .handle { params ->
        val request = params.body

        val result = authService.refresh(
            RefreshServiceReq(refreshToken = request.refreshToken),
        )

        when (result) {
            is RefreshServiceRes.Ok -> {
                RefreshHandlerResponse.Ok(
                    accessToken = result.accessToken,
                )
            }

            RefreshServiceRes.Error.RefreshTokenInvalid -> {
                RefreshHandlerResponse.ErrorInvalid(
                    message = "Invalid refresh token",
                )
            }

            RefreshServiceRes.Error.RefreshTokenExpired -> {
                RefreshHandlerResponse.ErrorExpired(
                    message = "Refresh token expired",
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
    @StatusCode.Ok
    data class Ok(
        val accessToken: String,
    ) : RefreshHandlerResponse()

    @Serializable
    @StatusCode.BadRequest
    data class ErrorInvalid(
        val message: String,
    ) : RefreshHandlerResponse()

    @Serializable
    @StatusCode.Unauthorized
    data class ErrorExpired(
        val message: String,
    ) : RefreshHandlerResponse()
}
