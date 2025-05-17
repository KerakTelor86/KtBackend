package setup

import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.util.logging.*
import org.koin.core.scope.Scope
import org.koin.dsl.module
import org.koin.ext.getFullName
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.typeOf

val configModule = module {
    single { readConfig() }
}

data class Config(
    val postgres: Postgres,
    val crypto: Crypto,
) {
    data class Postgres(
        val auth: Auth,
        val connection: Connection,
        val database: String,
    ) {
        data class Auth(
            val username: String,
            val password: String,
        )

        data class Connection(
            val host: String,
            val port: Int,
        )
    }

    data class Crypto(
        val bcrypt: BCrypt,
        val jwt: Jwt,
    ) {
        data class BCrypt(
            val rounds: Int,
        )

        data class Jwt(
            val secret: String,
            val issuer: String,
            val expirySeconds: Expiry,
        ) {
            data class Expiry(
                val access: Int,
                val refresh: Int,
            )
        }
    }
}

private val configLogger = KtorSimpleLogger("setup.config")

private fun Scope.readConfig(): Config {
    val application by inject<Application>()
    val configPathBuffer = mutableListOf<String>()

    fun getCurrentPath() = configPathBuffer
        .joinToString(".")

    fun getConfigAtCurrentPath() = application
        .environment
        .config
        .property(getCurrentPath())

    fun <T : Any> constructConfig(kClass: KClass<T>): T {
        val constructor = kClass.primaryConstructor
            ?: throw IllegalArgumentException(
                "Config is not a data class",
            )

        val parameters = constructor.parameters.associateWith {
            val key = it.name ?: throw IllegalArgumentException(
                "Config property does not have valid name",
            )

            configPathBuffer.add(key)
            try {
                getConfigAtCurrentPath().toValue(it.type)
            } catch (exception: Exception) {
                if (exception is ApplicationConfigurationException) {
                    // Ktor throws this when config at current path is a map
                    // We use recursion to dive into the map
                    constructConfig(
                        it.type.classifier!! as KClass<*>,
                    )
                } else {
                    throw IllegalArgumentException(
                        "Cannot read config property ${getCurrentPath()}",
                        exception,
                    )
                }
            } finally {
                configPathBuffer.removeLast()
            }
        }

        return constructor.callBy(parameters).also {
            configLogger.debug(
                "Constructed ${kClass.getFullName()} from config successfully",
            )
        }
    }

    return constructConfig(Config::class)
}

private fun ApplicationConfigValue.toValue(type: KType): Any = when (type) {
    typeOf<String>() -> this.getString()
    typeOf<Boolean>() -> this.getString().toBoolean()
    typeOf<Int>() -> this.getString().toInt()
    typeOf<Long>() -> this.getString().toLong()
    typeOf<Float>() -> this.getString().toFloat()
    typeOf<Double>() -> this.getString().toDouble()
    typeOf<List<String>>() -> this.getList()
    typeOf<List<Boolean>>() -> this.getList().map(String::toBoolean)
    typeOf<List<Int>>() -> this.getList().map(String::toInt)
    typeOf<List<Long>>() -> this.getList().map(String::toInt)
    typeOf<List<Float>>() -> this.getList().map(String::toFloat)
    typeOf<List<Double>>() -> this.getList().map(String::toDouble)

    else -> throw IllegalArgumentException(
        "Unhandled type while reading config ${
            (type.classifier!! as KClass<*>).getFullName()
        }",
    )
}
