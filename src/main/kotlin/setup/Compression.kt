package me.keraktelor.setup

import io.ktor.server.application.*
import io.ktor.server.plugins.compression.*

fun Application.setupCompression() {
    install(Compression)
}
