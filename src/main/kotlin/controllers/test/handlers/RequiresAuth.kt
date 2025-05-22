package controllers.test.handlers

import controllers.test.TestController
import kotlinx.serialization.Serializable
import plugins.getAccessTokenInfo
import utilities.routing.StatusCode
import utilities.routing.buildHandler

fun TestController.buildRequiresAuthHandler() = buildHandler
    .withQueryParams<RequiresAuthHandlerRequest>()
    .handle { params ->
        val tokenData = call.getAccessTokenInfo()
        val request = params.queryParams
        if (request.message == "ok") {
            RequiresAuthHandlerResponse.Ok(
                message = "$testPrefix - ${tokenData.userId}: '${
                    request.message
                }'",
            )
        } else {
            RequiresAuthHandlerResponse.Error(
                message = "bruh",
            )
        }
    }

@Serializable
data class RequiresAuthHandlerRequest(
    val message: String,
)

@Serializable
sealed class RequiresAuthHandlerResponse {
    @Serializable
    @StatusCode.Ok
    data class Ok(
        val message: String,
    ) : RequiresAuthHandlerResponse()

    @Serializable
    @StatusCode.BadRequest
    data class Error(
        val message: String,
    ) : RequiresAuthHandlerResponse()
}
