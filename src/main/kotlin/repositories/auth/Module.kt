package me.keraktelor.repositories.auth

import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import java.util.*

fun Module.authRepository() {
    singleOf<AuthRepository>(::AuthRepositoryImpl)
}

interface AuthRepository {
    fun create(user: User): User?

    fun findById(id: UUID): User?

    fun deleteById(id: UUID): User?

    fun findByUsername(username: String): User?
}
