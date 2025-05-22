package controllers.auth.handlers

import controllers.auth.AuthController
import kotlinx.serialization.Serializable
import plugins.RequiresValidation
import plugins.ValidationRequirement
import plugins.ValidationRequirement.Builder.validate
import services.auth.RegisterServiceReq
import services.auth.RegisterServiceRes
import utilities.routing.StatusCode
import utilities.routing.buildHandler

fun AuthController.buildRegisterHandler() = buildHandler
    .withBody<RegisterHandlerRequest>()
    .handle { params ->
        val request = params.body

        val result = authService.register(
            RegisterServiceReq(
                username = request.username,
                password = request.password,
            ),
        )

        when (result) {
            is RegisterServiceRes.Ok -> {
                RegisterHandlerResponse.Ok(
                    userId = result.userId,
                    accessToken = result.tokens.access,
                    refreshToken = result.tokens.refresh,
                )
            }

            is RegisterServiceRes.Error.DuplicateUser -> {
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
            validate("Username length should be within 6-32 characters") {
                username.length in 6..32
            },
            validate("Username should only contain alphanumeric characters") {
                username.all { it.isLetterOrDigit() }
            },
            validate("Password length should be at least 6 characters") {
                password.length >= 6
            },
        )
}

@Serializable
sealed class RegisterHandlerResponse {
    @Serializable
    @StatusCode.Created
    data class Ok(
        val userId: String,
        val accessToken: String,
        val refreshToken: String,
    ) : RegisterHandlerResponse()

    @Serializable
    @StatusCode.Conflict
    data class Error(
        val message: String,
    ) : RegisterHandlerResponse()
}
