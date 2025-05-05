package me.keraktelor.controllers.auth.handlers

import kotlinx.serialization.Serializable
import me.keraktelor.controllers.auth.AuthController
import me.keraktelor.services.auth.RegisterServiceReq
import me.keraktelor.services.auth.RegisterServiceRes
import me.keraktelor.utilities.dsl.Blank
import me.keraktelor.utilities.dsl.Handler.Builder.createHttpHandler
import me.keraktelor.utilities.dsl.Response.Builder.badRequest
import me.keraktelor.utilities.dsl.Response.Builder.ok
import me.keraktelor.utilities.validation.RequiresValidation
import me.keraktelor.utilities.validation.ValidationRequirement
import me.keraktelor.utilities.validation.ValidationRequirement.Builder.require

fun AuthController.getRegisterHandler() =
    createHttpHandler { _: Blank, request: RegisterHandlerRequest ->
        val result = authService.register(
            RegisterServiceReq(
                username = request.username,
                password = request.password,
            ),
        )

        when (result) {
            is RegisterServiceRes.Ok -> ok {
                RegisterHandlerResponse.Ok(
                    userId = result.userId,
                    accessToken = result.accessToken,
                )
            }

            is RegisterServiceRes.Error.DuplicateUser -> badRequest {
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
            require("Username length should be within 6-32 characters") {
                username.length in 6..32
            },
            require("Password length should be at least 6 characters") {
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
    ) : RegisterHandlerResponse()

    @Serializable
    data class Error(
        val message: String,
    ) : RegisterHandlerResponse()
}
