package services

import org.koin.dsl.module
import services.auth.authService

val serviceModule = module {
    authService()
}
