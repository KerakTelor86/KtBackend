package controllers.test.handlers

import controllers.test.TestController
import kotlinx.serialization.Serializable
import plugins.getAccessTokenInfo
import plugins.ok
import utilities.routing.buildHandler

fun TestController.buildRequiresAuthHandler() = buildHandler
    .withQueryParams<RequiresAuthHandlerRequest>()
    .handle { params ->
        val tokenData = call.getAccessTokenInfo()
        val request = params.queryParams
        ok {
            RequiresAuthHandlerResponse(
                message = "$testPrefix - ${tokenData.userId}: '${
                    request.message
                }'",
            )
        }
    }

@Serializable
data class RequiresAuthHandlerRequest(
    val message: String,
)

@Serializable
data class RequiresAuthHandlerResponse(
    val message: String,
)
