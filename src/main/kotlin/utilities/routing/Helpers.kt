package utilities.routing

import io.ktor.http.*
import kotlin.reflect.KType
import kotlin.reflect.full.primaryConstructor

@PublishedApi
internal inline fun <
    reified T : Any,
> getConstructorParams(): List<Pair<String, KType>> {
    val kClass = T::class

    val constructor = kClass.primaryConstructor
        ?: throw IllegalArgumentException(
            "Not a data class: ${
                kClass.qualifiedName.orEmpty()
            }",
        )

    return constructor.parameters.map {
        val name = it.name ?: throw IllegalStateException(
            "Found unnamed constructor parameter of type ${it.type}",
        )
        name to it.type
    }
}
