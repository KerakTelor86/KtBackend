package me.keraktelor.controllers.auth.handlers

import kotlinx.serialization.Serializable
import me.keraktelor.controllers.auth.AuthController
import me.keraktelor.services.auth.LoginServiceReq
import me.keraktelor.services.auth.LoginServiceRes
import me.keraktelor.utilities.dsl.Blank
import me.keraktelor.utilities.dsl.Handler.Builder.createHttpHandler
import me.keraktelor.utilities.dsl.Response.Builder.badRequest
import me.keraktelor.utilities.dsl.Response.Builder.ok

fun AuthController.getLoginHandler() =
    createHttpHandler { _: Blank, request: LoginHandlerRequest ->
        val result = this.authService.login(
            LoginServiceReq(
                username = request.username,
                password = request.password,
            ),
        )

        when (result) {
            is LoginServiceRes.Ok -> ok {
                LoginHandlerResponse.Ok(
                    userId = result.userId,
                    accessToken = result.accessToken,
                )
            }

            is LoginServiceRes.Error.InvalidCredentials -> badRequest {
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
    ) : LoginHandlerResponse()

    @Serializable
    data class Error(
        val message: String,
    ) : LoginHandlerResponse()
}
