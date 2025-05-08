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
    suspend fun create(username: String, password: String): User?

    suspend fun findById(id: UUID): User?

    suspend fun deactivateById(id: UUID): User?

    suspend fun findByUsername(username: String): User?
}
