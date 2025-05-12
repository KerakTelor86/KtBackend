package me.keraktelor.setup

import io.ktor.server.application.*
import me.keraktelor.controllers.controllerModule
import me.keraktelor.repositories.repositoryModule
import me.keraktelor.services.serviceModule
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun Application.setupInjection(
    overrideModules: Array<out Module> = emptyArray(),
) {
    install(Koin) {
        slf4jLogger()

        val applicationModule = module {
            single<Application> { this@setupInjection }
        }

        modules(
            applicationModule,
            configModule,
            databaseModule,
            repositoryModule,
            serviceModule,
            controllerModule,
            *overrideModules,
        )
    }
}
