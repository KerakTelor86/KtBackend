package me.keraktelor.handlers

import io.ktor.http.*
import io.ktor.server.plugins.requestvalidation.*
import kotlinx.serialization.Serializable
import me.keraktelor.utilities.dsl.Handler.Builder.createHttpHandler
import me.keraktelor.utilities.dsl.Handler.Builder.withExceptionHandler
import me.keraktelor.utilities.dsl.Response
import me.keraktelor.utilities.validation.Validatable

object TestHandlers {
    val handleTest = createHttpHandler { request: TestRequest ->
        when (request.answer) {
            42 -> TestResponse("You found the answer!")
            69 -> throw Exception("Nice")
            else -> throw TestError(42 - request.answer)
        }
    }.withExceptionHandler { err: TestError ->
        Response.Error(
            statusCode = HttpStatusCode.BadRequest,
            data = err
        )
    }
}

@Serializable
data class TestResponse(val message: String)

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
data class TestError(val difference: Int) : Exception()