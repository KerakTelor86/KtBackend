package me.keraktelor.handlers.auth.handlers

import io.ktor.http.*
import kotlinx.serialization.Serializable
import me.keraktelor.handlers.auth.AuthHandler
import me.keraktelor.services.auth.LoginServiceReq
import me.keraktelor.services.auth.LoginServiceRes
import me.keraktelor.utilities.dsl.Blank
import me.keraktelor.utilities.dsl.Handler.Builder.createHttpHandler
import me.keraktelor.utilities.dsl.Response.Builder.err
import me.keraktelor.utilities.dsl.Response.Builder.ok

fun AuthHandler.getLoginHandler() =
    createHttpHandler { _: Blank, request: LoginHandlerRequest ->
        val result = this.authService.login(
            LoginServiceReq(
                username = request.username,
                password = request.password,
            ),
        )

        when (result) {
            is LoginServiceRes.Ok ->
                ok {
                    LoginHandlerResponse(
                        userId = result.userId,
                        accessToken = result.accessToken,
                    )
                }

            is LoginServiceRes.Error.InvalidCredentials ->
                err(HttpStatusCode.BadRequest) {
                    LoginHandlerError(
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
data class LoginHandlerResponse(
    val userId: String,
    val accessToken: String,
)

@Serializable
data class LoginHandlerError(
    val message: String,
)
