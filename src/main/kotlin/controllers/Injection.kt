package me.keraktelor.controllers

import me.keraktelor.controllers.auth.authController
import org.koin.dsl.module

val controllerModule = module {
    authController()
}
