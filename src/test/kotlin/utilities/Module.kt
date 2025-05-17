@file:Suppress("unused")

package utilities

import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.koin.core.logger.Level
import org.koin.core.module.Module
import org.koin.core.scope.Scope
import org.koin.dsl.koinApplication
import org.koin.logger.slf4jLogger
import org.koin.dsl.module as koinModule

private typealias ModuleBody = Module.() -> Unit
private typealias TestBody = suspend Scope.() -> Unit

class TestModuleBuilder internal constructor() {
    @PublishedApi
    internal val moduleLambdas = mutableListOf<ModuleBody>()
    internal val testLambdas = mutableListOf<TestBody>()

    fun module(body: ModuleBody) {
        moduleLambdas.add(body)
    }

    inline fun <reified T : Any> mock() {
        moduleLambdas.add {
            single { mockk<T>() }
        }
    }

    fun execute(body: TestBody) {
        if (testLambdas.isNotEmpty()) {
            throw IllegalStateException(
                "Test module cannot have more than one execute block",
            )
        }
        testLambdas.add(body)
    }
}

fun buildTestModule(body: TestModuleBuilder.() -> Unit) {
    val builder = TestModuleBuilder().apply(body)

    koinApplication {
        slf4jLogger(level = Level.NONE)
        modules(
            koinModule {
                builder.moduleLambdas.forEach { it() }
            },
            koinModule {
                single(createdAtStart = true) {
                    runBlocking {
                        builder.testLambdas.forEach { it() }
                    }
                }
            },
        )
    }
}
