package plugins

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.util.*
import services.auth.AuthService
import services.auth.DecodeTokenServiceReq
import services.auth.DecodeTokenServiceRes

class AuthRoutingPluginConfig {
    var authService: AuthService? = null
}

typealias DecodedToken = DecodeTokenServiceRes.Ok

private val CALL_ATTRIBUTE_KEY = AttributeKey<DecodedToken>(
    "decodedToken",
)

class UnauthorizedException(
    override val message: String,
) : Exception(message)

val RequiresAuth = createRouteScopedPlugin(
    name = "AuthRoutingPlugin",
    createConfiguration = ::AuthRoutingPluginConfig,
) {
    pluginConfig.apply {
        val authService = pluginConfig.authService
            ?: throw IllegalArgumentException(
                "No valid auth service found in config",
            )

        onCall { call ->
            val token = call.request.headers["Authorization"]
                ?: throw UnauthorizedException("No Authorization header found")
            val decoded = authService.decode(
                DecodeTokenServiceReq(
                    accessToken = token.removePrefix("Bearer "),
                ),
            )

            when (decoded) {
                is DecodedToken -> call.attributes.put(
                    CALL_ATTRIBUTE_KEY,
                    decoded,
                )

                DecodeTokenServiceRes.Error.AccessTokenExpired -> {
                    throw UnauthorizedException("Access token has expired")
                }

                DecodeTokenServiceRes.Error.AccessTokenInvalid -> {
                    throw UnauthorizedException("Invalid access token")
                }
            }
        }
    }
}

fun RoutingCall.getAccessTokenInfo(): DecodedToken =
    attributes[CALL_ATTRIBUTE_KEY]
