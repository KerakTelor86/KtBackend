package me.keraktelor.utilities.validation

import io.ktor.server.plugins.requestvalidation.*

interface Validatable {
    fun validate(): ValidationResult
}