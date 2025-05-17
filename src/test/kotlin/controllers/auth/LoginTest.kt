package controllers.auth

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import me.keraktelor.controllers.auth.handlers.LoginHandlerRequest
import me.keraktelor.controllers.auth.handlers.LoginHandlerResponse
import me.keraktelor.module
import me.keraktelor.services.auth.AuthService
import me.keraktelor.services.auth.AuthToken
import me.keraktelor.services.auth.LoginServiceReq
import me.keraktelor.services.auth.LoginServiceRes
import org.koin.dsl.module
import utilities.getTestConfig
import utilities.getTestDatabase
import kotlin.test.Test

class LoginTest {
    @Test
    fun testSuccessfulLogin() = loginTest { client, service ->
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
    fun testInvalidCredentials() = loginTest { client, service ->
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

        fun loginTest(
            body: suspend (
                client: HttpClient,
                service: AuthService,
            ) -> Unit,
        ) = testApplication {
            val service = mockk<AuthService>()

            application {
                module(
                    module {
                        single { getTestConfig() }
                        single { getTestDatabase() }
                        single { service }
                    },
                )
            }

            val client = client.config {
                install(ContentNegotiation) {
                    json()
                }
                defaultRequest {
                    contentType(ContentType.Application.Json)
                }
            }

            body(client, service)
        }
    }
}
