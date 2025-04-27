package me.keraktelor.utilities.routing

import io.ktor.http.*
import kotlinx.serialization.json.Json

inline fun <reified T : Any> Parameters.toDataClass(): T {
    val kClass = T::class
    val props = kClass
        .members
        .filterIsInstance<kotlin.reflect.KProperty1<*, *>>()

    val missingParameters = mutableListOf<String>()

    val paramMap = props.associate {
        if (this[it.name] == null) {
            missingParameters.add(it.name)
        }
        it.name to this[it.name].orEmpty()
    }

    if (missingParameters.isNotEmpty()) {
        throw MissingParameterException(missingParameters)
    }

    return Json.decodeFromString(
        Json.encodeToString(
            paramMap
        )
    )
}
