package controllers.auth.handlers

import controllers.auth.AuthController
import kotlinx.serialization.Serializable
import services.auth.LoginServiceReq
import services.auth.LoginServiceRes
import utilities.routing.StatusCode
import utilities.routing.buildHandler

fun AuthController.buildLoginHandler() = buildHandler
    .withBody<LoginHandlerRequest>()
    .handle { params ->
        val request = params.body

        val result = authService.login(
            LoginServiceReq(
                username = request.username,
                password = request.password,
            ),
        )

        when (result) {
            is LoginServiceRes.Ok -> {
                LoginHandlerResponse.Ok(
                    userId = result.userId,
                    accessToken = result.tokens.access,
                    refreshToken = result.tokens.refresh,
                )
            }

            LoginServiceRes.Error.InvalidCredentials -> {
                LoginHandlerResponse.Error(
                    message = "Invalid credentials",
                )
            }
        }
    }

@Serializable
data class LoginHandlerRequest(
    val username: String,
    val password: String,
)

@Serializable
sealed class LoginHandlerResponse {
    @Serializable
    @StatusCode.Ok
    data class Ok(
        val userId: String,
        val accessToken: String,
        val refreshToken: String,
    ) : LoginHandlerResponse()

    @Serializable
    @StatusCode.Unauthorized
    data class Error(
        val message: String,
    ) : LoginHandlerResponse()
}
