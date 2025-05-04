package me.keraktelor.handlers.auth.handlers

import me.keraktelor.handlers.auth.AuthHandler
import me.keraktelor.utilities.dsl.Blank
import me.keraktelor.utilities.dsl.Handler.Builder.createHttpHandler
import me.keraktelor.utilities.dsl.Response.Builder.internalServerError

fun AuthHandler.getRegisterHandler() =
    createHttpHandler { _: Blank, _: Blank ->
        internalServerError { "Unimplemented" }
    }
