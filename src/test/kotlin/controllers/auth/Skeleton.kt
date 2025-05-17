package controllers.auth

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import io.mockk.mockk
import module
import services.auth.AuthService
import utilities.getTestConfig
import utilities.getTestDatabase

fun authHandlerTest(
    body: suspend (
        client: HttpClient,
        service: AuthService,
    ) -> Unit,
) = testApplication {
    val service = mockk<AuthService>()

    application {
        module(
            org.koin.dsl.module {
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
