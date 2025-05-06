package me.keraktelor

import io.ktor.server.application.*
import io.ktor.server.cio.*
import me.keraktelor.setup.setupCompression
import me.keraktelor.setup.setupInjection
import me.keraktelor.setup.setupRouting
import me.keraktelor.setup.setupSerialization

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    setupInjection()
    setupSerialization()
    setupCompression()
    setupRouting()
}
