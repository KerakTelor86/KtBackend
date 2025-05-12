package me.keraktelor

import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import me.keraktelor.utils.testing.getTestConfig
import me.keraktelor.utils.testing.getTestDatabase
import org.koin.dsl.module
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {
    @Test
    fun testRoot() = testApplication {
        val testDbModule = module {
            single { getTestConfig() }
            single { getTestDatabase() }
        }
        application {
            module(arrayOf(testDbModule))
        }
        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
        }
    }
}
