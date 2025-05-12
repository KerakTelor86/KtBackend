package me.keraktelor

import io.ktor.server.application.*
import io.ktor.server.netty.*
import me.keraktelor.setup.setupCompression
import me.keraktelor.setup.setupInjection
import me.keraktelor.setup.setupRouting
import me.keraktelor.setup.setupSerialization
import org.koin.core.module.Module

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module(
    overrideModules: Array<Module> = emptyArray(),
) {
    setupInjection(overrideModules)
    setupSerialization()
    setupCompression()
    setupRouting()
}
