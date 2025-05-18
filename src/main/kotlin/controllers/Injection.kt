package controllers

import controllers.auth.authController
import controllers.test.testController
import org.koin.dsl.module

val controllerModule = module(createdAtStart = true) {
    authController()
    testController()
}
