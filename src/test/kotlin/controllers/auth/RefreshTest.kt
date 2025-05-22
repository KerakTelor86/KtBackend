package controllers.auth

import controllers.auth.handlers.RefreshHandlerRequest
import controllers.auth.handlers.RefreshHandlerResponse
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.mockk.coEvery
import io.mockk.coVerify
import services.auth.RefreshServiceReq
import services.auth.RefreshServiceRes
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RefreshTest {
    @Test
    fun testSuccessfulRefresh() = authHandlerTest { client, service ->
        val serviceReq = RefreshServiceReq(
            refreshToken = "refresh",
        )
        val serviceRes = RefreshServiceRes.Ok(
            accessToken = "access",
        )
        coEvery { service.refresh(serviceReq) }.returns(serviceRes)

        val response = client.post(ROUTE) {
            setBody(
                RefreshHandlerRequest(
                    refreshToken = "refresh",
                ),
            )
        }
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(
            RefreshHandlerResponse.Ok(
                accessToken = "access",
            ),
            response.body<RefreshHandlerResponse.Ok>(),
        )

        coVerify(exactly = 1) { service.refresh(serviceReq) }
    }

    @Test
    fun testInvalidToken() = authHandlerTest { client, service ->
        val serviceReq = RefreshServiceReq(
            refreshToken = "refresh",
        )
        val serviceRes = RefreshServiceRes.Error.RefreshTokenInvalid
        coEvery { service.refresh(serviceReq) }.returns(serviceRes)

        val response = client.post(ROUTE) {
            setBody(
                RefreshHandlerRequest(
                    refreshToken = "refresh",
                ),
            )
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertTrue {
            response
                .body<RefreshHandlerResponse.ErrorInvalid>()
                .message
                .lowercase()
                .contains("invalid")
        }

        coVerify(exactly = 1) { service.refresh(serviceReq) }
    }

    @Test
    fun testExpiredToken() = authHandlerTest { client, service ->
        val serviceReq = RefreshServiceReq(
            refreshToken = "refresh",
        )
        val serviceRes = RefreshServiceRes.Error.RefreshTokenExpired
        coEvery { service.refresh(serviceReq) }.returns(serviceRes)

        val response = client.post(ROUTE) {
            setBody(
                RefreshHandlerRequest(
                    refreshToken = "refresh",
                ),
            )
        }
        assertEquals(HttpStatusCode.Unauthorized, response.status)
        assertTrue {
            response
                .body<RefreshHandlerResponse.ErrorExpired>()
                .message
                .lowercase()
                .contains("expired")
        }

        coVerify(exactly = 1) { service.refresh(serviceReq) }
    }

    private companion object {
        const val ROUTE = "/auth/refresh"
    }
}
