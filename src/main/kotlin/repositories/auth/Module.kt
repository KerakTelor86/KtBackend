package me.keraktelor.repositories.auth

import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import java.util.*

fun Module.authRepository() {
    singleOf(::AuthRepositoryImpl) {
        bind<AuthRepository>()
    }
}

interface AuthRepository {
    fun create(username: String, password: String): User?

    fun findById(id: UUID): User?

    fun deactivateById(id: UUID): User?

    fun findByUsername(username: String): User?
}
