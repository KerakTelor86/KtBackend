package controllers.auth

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.mockk.coEvery
import me.keraktelor.controllers.auth.handlers.RegisterHandlerRequest
import me.keraktelor.controllers.auth.handlers.RegisterHandlerResponse
import me.keraktelor.plugins.RequestValidationExceptionResponse
import me.keraktelor.services.auth.AuthToken
import me.keraktelor.services.auth.RegisterServiceReq
import me.keraktelor.services.auth.RegisterServiceRes
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RegisterTest {
    @Test
    fun testSuccessfulRegistration() = authHandlerTest { client, service ->
        val serviceReq = RegisterServiceReq(
            username = "validUsername",
            password = "validPassword",
        )
        val serviceRes = RegisterServiceRes.Ok(
            userId = "userId",
            tokens = AuthToken(
                access = "access",
                refresh = "refresh",
            ),
        )
        coEvery { service.register(serviceReq) }.returns(serviceRes)

        val response = client.post(ROUTE) {
            setBody(
                RegisterHandlerRequest(
                    username = "validUsername",
                    password = "validPassword",
                ),
            )
        }
        assertEquals(HttpStatusCode.Created, response.status)
        assertEquals(
            RegisterHandlerResponse.Ok(
                userId = "userId",
                accessToken = "access",
                refreshToken = "refresh",
            ),
            response.body<RegisterHandlerResponse.Ok>(),
        )
    }

    @Test
    fun testDuplicateUser() = authHandlerTest { client, service ->
        val serviceReq = RegisterServiceReq(
            username = "validUsername",
            password = "validPassword",
        )
        val serviceRes = RegisterServiceRes.Error.DuplicateUser

        coEvery { service.register(serviceReq) }.returns(serviceRes)

        val response = client.post(ROUTE) {
            setBody(
                RegisterHandlerRequest(
                    username = "validUsername",
                    password = "validPassword",
                ),
            )
        }
        assertEquals(HttpStatusCode.Conflict, response.status)
        assertEquals(
            RegisterHandlerResponse.Error(
                message = "Username already exists",
            ),
            response.body<RegisterHandlerResponse.Error>(),
        )
    }

    @Test
    fun testInvalidBody() = authHandlerTest { client, _ ->
        val response = client.post(ROUTE) {
            setBody(
                RegisterHandlerRequest(
                    username = "a",
                    password = "1",
                ),
            )
        }
        assertEquals(
            HttpStatusCode.BadRequest,
            response.status,
        )
        val reasons =
            response.body<RequestValidationExceptionResponse>().reasons
        assertTrue {
            reasons.any { reason ->
                reason.lowercase().contains("username")
            }
        }
        assertTrue {
            reasons.any { reason ->
                reason.lowercase().contains("password")
            }
        }
    }

    private companion object {
        const val ROUTE = "/auth/register"
    }
}
