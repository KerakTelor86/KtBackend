package me.keraktelor.controllers.auth.handlers

import kotlinx.serialization.Serializable
import me.keraktelor.controllers.auth.AuthController
import me.keraktelor.services.auth.RefreshServiceReq
import me.keraktelor.services.auth.RefreshServiceRes
import me.keraktelor.utilities.dsl.Blank
import me.keraktelor.utilities.dsl.Handler.Builder.createHttpHandler
import me.keraktelor.utilities.dsl.Response.Builder.ok

fun AuthController.getRefreshHandler() =
    createHttpHandler { _: Blank, request: RefreshHandlerRequest ->
        val result = this.authService.refresh(
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
