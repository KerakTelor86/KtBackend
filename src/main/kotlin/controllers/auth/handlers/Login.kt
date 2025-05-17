package me.keraktelor.controllers.auth.handlers

import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import me.keraktelor.controllers.auth.AuthController
import me.keraktelor.plugins.ok
import me.keraktelor.plugins.receiveValidatedBody
import me.keraktelor.plugins.unauthorized
import me.keraktelor.services.auth.LoginServiceReq
import me.keraktelor.services.auth.LoginServiceRes

suspend fun AuthController.handleLogin(
    context: RoutingContext,
) = with(context) {
    val request = call.receiveValidatedBody<LoginHandlerRequest>()

    val result = authService.login(
        LoginServiceReq(
            username = request.username,
            password = request.password,
        ),
    )

    when (result) {
        is LoginServiceRes.Ok -> ok {
            LoginHandlerResponse.Ok(
                userId = result.userId,
                accessToken = result.tokens.access,
                refreshToken = result.tokens.refresh,
            )
        }

        LoginServiceRes.Error.InvalidCredentials -> unauthorized {
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
    data class Ok(
        val userId: String,
        val accessToken: String,
        val refreshToken: String,
    ) : LoginHandlerResponse()

    @Serializable
    data class Error(
        val message: String,
    ) : LoginHandlerResponse()
}
