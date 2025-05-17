@file:Suppress("unused")

package me.keraktelor.plugins

import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.util.*
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.typeOf

suspend inline fun <reified T : Any> RoutingCall.receiveValidatedHeaders(): T =
    request.headers.toMap().toDataClass<T>().also {
        if (it is RequiresValidation) {
            it.validate()
        }
    }

suspend inline fun <reified T : Any> RoutingCall.receiveValidatedPath(): T =
    pathParameters.toMap().toDataClass<T>().also {
        if (it is RequiresValidation) {
            it.validate()
        }
    }

suspend inline fun <reified T : Any> RoutingCall.receiveValidatedQuery(): T =
    queryParameters.toMap().toDataClass<T>().also {
        if (it is RequiresValidation) {
            it.validate()
        }
    }

suspend inline fun <reified T : Any> RoutingCall.receiveValidatedBody(): T =
    this.receive<T>().also {
        if (it is RequiresValidation) {
            it.validate()
        }
    }

class DataClassTransformationException(
    kClass: KClass<*>,
    cause: Throwable? = null,
) : Exception(
        "Data class transformation not supported for ${
            kClass.qualifiedName.orEmpty()
        }",
        cause,
    )

@PublishedApi
internal inline fun <
    reified T : Any,
> Map<String, List<String>>.toDataClass(): T {
    val kClass = T::class

    val constructor = kClass.primaryConstructor
        ?: throw DataClassTransformationException(kClass)

    val missingParameters = mutableListOf<String>()
    val parameters = constructor.parameters.associateWith {
        val key = it.name.orEmpty()
        val value = this[key]?.firstOrNull().orEmpty()
        try {
            when (it.type) {
                typeOf<String>() -> value
                typeOf<Int>() -> value.toInt()
                typeOf<Long>() -> value.toLong()
                typeOf<Float>() -> value.toFloat()
                typeOf<Double>() -> value.toDouble()
                else -> throw DataClassTransformationException(
                    kClass,
                    IllegalArgumentException("Type not supported: ${it.type}"),
                )
            }
        } catch (_: NumberFormatException) {
            if (!it.type.isMarkedNullable) {
                missingParameters.add(key)
            }
            null
        }
    }

    if (missingParameters.isNotEmpty()) {
        throw RequestValidationException(
            reasons = missingParameters.map {
                "Missing field: '$it'"
            },
        )
    }

    return constructor.callBy(parameters)
}
