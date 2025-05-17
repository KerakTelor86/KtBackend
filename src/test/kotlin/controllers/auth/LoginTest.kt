package controllers.auth

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.mockk.coEvery
import io.mockk.coVerify
import me.keraktelor.controllers.auth.handlers.LoginHandlerRequest
import me.keraktelor.controllers.auth.handlers.LoginHandlerResponse
import me.keraktelor.services.auth.AuthToken
import me.keraktelor.services.auth.LoginServiceReq
import me.keraktelor.services.auth.LoginServiceRes
import kotlin.test.Test
import kotlin.test.assertEquals

class LoginTest {
    @Test
    fun testSuccessfulLogin() = authHandlerTest { client, service ->
        val serviceReq = LoginServiceReq(
            username = "user",
            password = "password",
        )
        val serviceRes = LoginServiceRes.Ok(
            userId = "id",
            tokens = AuthToken(
                access = "access",
                refresh = "refresh",
            ),
        )
        coEvery { service.login(serviceReq) }.returns(serviceRes)

        val response = client.post(ROUTE) {
            setBody(
                LoginHandlerRequest(
                    username = "user",
                    password = "password",
                ),
            )
        }
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(
            LoginHandlerResponse.Ok(
                userId = "id",
                accessToken = "access",
                refreshToken = "refresh",
            ),
            response.body<LoginHandlerResponse.Ok>(),
        )

        coVerify(exactly = 1) { service.login(serviceReq) }
    }

    @Test
    fun testInvalidCredentials() = authHandlerTest { client, service ->
        val serviceReq = LoginServiceReq(
            username = "user",
            password = "password",
        )
        val serviceRes = LoginServiceRes.Error.InvalidCredentials
        coEvery { service.login(serviceReq) }.returns(serviceRes)

        val result = client.post(ROUTE) {
            setBody(
                LoginHandlerRequest(
                    username = "user",
                    password = "password",
                ),
            )
        }
        assertEquals(HttpStatusCode.Unauthorized, result.status)
        assertEquals(
            LoginHandlerResponse.Error(
                message = "Invalid credentials",
            ),
            result.body<LoginHandlerResponse.Error>(),
        )

        coVerify(exactly = 1) { service.login(serviceReq) }
    }

    private companion object {
        const val ROUTE = "/auth/login"
    }
}
