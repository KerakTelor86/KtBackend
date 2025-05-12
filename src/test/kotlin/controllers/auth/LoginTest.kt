package me.keraktelor.controllers.auth

import io.ktor.http.*
import io.mockk.coEvery
import io.mockk.coVerify
import junit.framework.TestCase.assertEquals
import me.keraktelor.controllers.auth.handlers.LoginHandlerRequest
import me.keraktelor.controllers.auth.handlers.LoginHandlerResponse
import me.keraktelor.controllers.auth.handlers.getLoginHandler
import me.keraktelor.services.auth.AuthService
import me.keraktelor.services.auth.AuthToken
import me.keraktelor.services.auth.LoginServiceReq
import me.keraktelor.services.auth.LoginServiceRes
import me.keraktelor.utilities.dsl.Blank
import me.keraktelor.utilities.dsl.Request
import me.keraktelor.utilities.dsl.Response
import me.keraktelor.utils.testing.buildTestModule
import kotlin.test.Test

class LoginTest {
    @Test
    fun testSuccessfulLogin() = loginTest { controller, service ->
        val handler = controller.getLoginHandler()

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

        val result = handler(
            Request(
                pathParams = Blank(),
                data = LoginHandlerRequest(
                    username = "user",
                    password = "password",
                ),
            ),
        )
        assertEquals(
            Response(
                statusCode = HttpStatusCode.OK,
                data = LoginHandlerResponse.Ok(
                    userId = "id",
                    accessToken = "access",
                    refreshToken = "refresh",
                ),
            ),
            result,
        )

        coVerify(exactly = 1) { service.login(serviceReq) }
    }

    @Test
    fun testInvalidCredentials() = loginTest { controller, service ->
        val handler = controller.getLoginHandler()

        val serviceReq = LoginServiceReq(
            username = "user",
            password = "password",
        )
        val serviceRes = LoginServiceRes.Error.InvalidCredentials

        coEvery { service.login(serviceReq) }.returns(serviceRes)

        val result = handler(
            Request(
                pathParams = Blank(),
                data = LoginHandlerRequest(
                    username = "user",
                    password = "password",
                ),
            ),
        )
        assertEquals(
            Response(
                statusCode = HttpStatusCode.Unauthorized,
                data = LoginHandlerResponse.Error(
                    message = "Invalid credentials",
                ),
            ),
            result,
        )

        coVerify(exactly = 1) { service.login(serviceReq) }
    }

    private fun loginTest(
        body: suspend (
            controller: AuthController,
            service: AuthService,
        ) -> Unit,
    ) = buildTestModule {
        mock<AuthService>()
        module {
            authController()
        }
        execute {
            val controller by inject<AuthController>()
            val service by inject<AuthService>()
            body(controller, service)
        }
    }
}
