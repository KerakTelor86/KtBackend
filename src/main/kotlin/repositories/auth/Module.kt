package me.keraktelor.repositories.auth

import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf

fun Module.authRepository() {
    singleOf<AuthRepository>(::AuthRepositoryImpl)
}

interface AuthRepository
