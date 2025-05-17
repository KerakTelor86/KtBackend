package controllers

import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import module
import org.koin.dsl.module
import utilities.getTestConfig
import utilities.getTestDatabase
import kotlin.test.Test
import kotlin.test.assertEquals

class DefaultRoutesTest {
    @Test
    fun testRoot() = testApplication {
        application {
            module(
                module {
                    single { getTestConfig() }
                    single { getTestDatabase() }
                },
            )
        }
        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
        }
    }
}
