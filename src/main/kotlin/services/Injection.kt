package me.keraktelor.services

import me.keraktelor.services.auth.authService
import org.koin.dsl.module

val serviceModule = module {
    authService()
}
