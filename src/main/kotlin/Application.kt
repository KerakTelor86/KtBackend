package me.keraktelor

import io.ktor.server.application.*
import io.ktor.server.cio.*
import me.keraktelor.setup.*

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    setupInjection()
    setupDatabase()
    setupSerialization()
    setupCompression()
    setupRouting()
}
