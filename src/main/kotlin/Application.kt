import io.ktor.server.application.*
import io.ktor.server.netty.*
import org.koin.core.module.Module
import setup.setupInjection
import setup.setupRouting

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module(
    vararg overrideModules: Module = emptyArray(),
) {
    setupInjection(overrideModules)
    setupRouting()
}
