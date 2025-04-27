package me.keraktelor.utilities.routing

import io.ktor.http.*
import kotlinx.serialization.json.Json

inline fun <reified T : Any> Parameters.toDataClass(): T {
    val kClass = T::class
    val props = kClass
        .members
        .filterIsInstance<kotlin.reflect.KProperty1<*, *>>()

    val missingParameters = mutableListOf<String>()

    val paramMap = props.mapNotNull {
        if (this[it.name] == null) {
            missingParameters.add(it.name)
            return@mapNotNull null
        }
        it.name to this[it.name]
    }.toMap()

    if (missingParameters.isNotEmpty()) {
        throw MissingParametersException(missingParameters)
    }

    try {
        return Json.decodeFromString(
            Json.encodeToString(
                paramMap
            )
        )
    } catch (e: Exception) {
        throw MalformedParametersException(e)
    }
}
