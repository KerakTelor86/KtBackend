package me.keraktelor.repositories

import me.keraktelor.repositories.auth.authRepository
import org.koin.dsl.module

val repositoryModule = module {
    authRepository()
}
