package me.keraktelor.utils.testing

import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.koin.core.logger.Level
import org.koin.core.module.Module
import org.koin.core.scope.Scope
import org.koin.dsl.koinApplication
import org.koin.logger.slf4jLogger
import org.koin.dsl.module as koinModule

private typealias Body = suspend Scope.() -> Unit

class TestModuleBuilder internal constructor() {
    @PublishedApi
    internal val modules = mutableListOf<Module>()
    internal val executables = mutableListOf<Body>()

    fun module(body: Module.() -> Unit) {
        modules.add(
            koinModule {
                body()
            },
        )
    }

    inline fun <reified T : Any> mock() {
        modules.add(
            koinModule {
                single { mockk<T>() }
            },
        )
    }

    fun execute(body: Body) {
        executables.add(body)
    }
}

fun buildTestModule(body: TestModuleBuilder.() -> Unit) {
    val builder = TestModuleBuilder().apply(body)

    koinApplication {
        slf4jLogger(level = Level.NONE)
        modules(
            *builder.modules.toTypedArray(),
            koinModule {
                single(createdAtStart = true) {
                    runBlocking {
                        builder.executables.forEach { it() }
                    }
                }
            },
        )
    }
}
