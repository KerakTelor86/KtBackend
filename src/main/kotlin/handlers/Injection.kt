package me.keraktelor.handlers

import me.keraktelor.handlers.auth.authHandler
import org.koin.dsl.module

val handlerModule = module {
    authHandler()
}
