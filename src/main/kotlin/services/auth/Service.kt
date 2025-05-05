package me.keraktelor.services.auth

import me.keraktelor.repositories.auth.AuthRepository
import me.keraktelor.repositories.auth.User
import org.jetbrains.exposed.sql.transactions.transaction

internal class AuthServiceImpl(
    private val authRepository: AuthRepository,
) : AuthService {
    override suspend fun register(
        request: RegisterServiceReq,
    ): RegisterServiceRes = transaction {
        val user = authRepository.create(
            User(request.username, request.password),
        ) ?: return@transaction RegisterServiceRes.Error.DuplicateUser

        RegisterServiceRes.Ok(
            userId = user.id.toString(),
            tokens = generateTokens(user),
        )
    }

    override suspend fun login(request: LoginServiceReq): LoginServiceRes {
        TODO("Not yet implemented")
    }

    override suspend fun refresh(
        request: RefreshServiceReq,
    ): RefreshServiceRes {
        TODO("Not yet implemented")
    }

    private fun generateTokens(user: User): AuthToken {
        TODO("Not yet implemented")
    }
}
