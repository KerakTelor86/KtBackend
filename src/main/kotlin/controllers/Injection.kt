package controllers

import controllers.auth.authController
import org.koin.dsl.module

val controllerModule = module(createdAtStart = true) {
    authController()
}
