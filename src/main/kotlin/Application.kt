package me.keraktelor

import io.ktor.server.application.*
import me.keraktelor.setup.*

fun main(args: Array<String>) {
    io.ktor.server.cio.EngineMain.main(args)
}

fun Application.module() {
    setupInjection()
    setupDatabase()
    setupSerialization()
    setupCompression()
    setupRouting()
}
