package me.keraktelor.setup

import io.ktor.server.application.*
import me.keraktelor.controllers.controllerModule
import me.keraktelor.repositories.repositoryModule
import me.keraktelor.services.serviceModule
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun Application.setupInjection() {
    install(Koin) {
        slf4jLogger()
        modules(controllerModule, serviceModule, repositoryModule)
    }
}
