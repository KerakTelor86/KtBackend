package me.keraktelor

import io.ktor.server.application.*
import io.ktor.server.netty.*
import me.keraktelor.setup.setupInjection
import me.keraktelor.setup.setupRouting
import org.koin.core.module.Module

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module(
    vararg overrideModules: Module = emptyArray(),
) {
    setupRouting()
    setupInjection(overrideModules)
}
