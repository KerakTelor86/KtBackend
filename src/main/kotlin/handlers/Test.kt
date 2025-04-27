package me.keraktelor.handlers

import io.ktor.server.plugins.requestvalidation.*
import kotlinx.serialization.Serializable
import me.keraktelor.utilities.routing.Handler
import me.keraktelor.utilities.routing.Response.Builder.badRequest
import me.keraktelor.utilities.routing.Response.Builder.ok
import me.keraktelor.utilities.routing.Response.Builder.serverError
import me.keraktelor.utilities.validation.Validatable

object TestHandlers {
    val handleTest: Handler<TestRequest, String, TestError> = { request ->
        when (request.answer) {
            42 -> ok { "You found the answer!" }
            69 -> serverError { Exception("Nice") }
            else -> badRequest { TestError(42 - request.answer) }
        }
    }
}

@Serializable
data class TestRequest(val answer: Int) : Validatable {
    override fun validate(): ValidationResult {
        if (answer < 0) {
            return ValidationResult.Invalid("'answer' should not be negative.")
        }
        return ValidationResult.Valid
    }
}

@Serializable
data class TestError(val difference: Int)