package controllers.auth.handlers

import controllers.auth.AuthController
import io.github.smiley4.ktoropenapi.config.RouteConfig
import io.ktor.http.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import plugins.ok
import plugins.receiveValidatedBody
import plugins.unauthorized
import services.auth.LoginServiceReq
import services.auth.LoginServiceRes

fun RouteConfig.loginInfo() {
    description = "Login"
    tags("auth")
    request {
        body<LoginHandlerRequest>()
    }
    response {
        code(HttpStatusCode.OK) {
            description = "OK"
            body<LoginHandlerResponse.Ok>()
        }
        code(HttpStatusCode.Unauthorized) {
            description = "Invalid credentials"
            body<LoginHandlerResponse.Error>()
        }
    }
}

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
