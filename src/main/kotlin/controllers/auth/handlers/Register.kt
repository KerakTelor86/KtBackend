package controllers.auth.handlers

import controllers.auth.AuthController
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import plugins.*
import plugins.ValidationRequirement.Builder.verify
import services.auth.RegisterServiceReq
import services.auth.RegisterServiceRes

suspend fun AuthController.handleRegister(
    context: RoutingContext,
) = with(context) {
    val request = call.receiveValidatedBody<RegisterHandlerRequest>()

    val result = authService.register(
        RegisterServiceReq(
            username = request.username,
            password = request.password,
        ),
    )

    when (result) {
        is RegisterServiceRes.Ok -> created {
            RegisterHandlerResponse.Ok(
                userId = result.userId,
                accessToken = result.tokens.access,
                refreshToken = result.tokens.refresh,
            )
        }

        is RegisterServiceRes.Error.DuplicateUser -> conflict {
            RegisterHandlerResponse.Error(
                message = "Username already exists",
            )
        }
    }
}

@Serializable
data class RegisterHandlerRequest(
    val username: String,
    val password: String,
) : RequiresValidation {
    override val requirements: List<ValidationRequirement>
        get() = listOf(
            verify("Username length should be within 6-32 characters") {
                username.length in 6..32
            },
            verify("Username should only contain alphanumeric characters") {
                username.all { it.isLetterOrDigit() }
            },
            verify("Password length should be at least 6 characters") {
                password.length >= 6
            },
        )
}

@Serializable
sealed class RegisterHandlerResponse {
    @Serializable
    data class Ok(
        val userId: String,
        val accessToken: String,
        val refreshToken: String,
    ) : RegisterHandlerResponse()

    @Serializable
    data class Error(
        val message: String,
    ) : RegisterHandlerResponse()
}
