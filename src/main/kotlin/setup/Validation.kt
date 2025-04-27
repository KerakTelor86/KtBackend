package me.keraktelor.setup

import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*
import me.keraktelor.utilities.validation.Validatable

fun Application.setupValidation() {
    install(RequestValidation) {
        validate<Validatable> {
            it.validate()
        }
    }
}