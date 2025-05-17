package setup

import controllers.controllerModule
import io.ktor.server.application.*
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import repositories.repositoryModule
import services.serviceModule

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
