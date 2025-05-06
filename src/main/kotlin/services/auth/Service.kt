package me.keraktelor.services.auth

import me.keraktelor.repositories.auth.AuthRepository
import me.keraktelor.repositories.auth.User

internal class AuthServiceImpl(
    private val authRepository: AuthRepository,
) : AuthService {
    override suspend fun register(
        request: RegisterServiceReq,
    ): RegisterServiceRes {
        val user = authRepository.create(
            username = request.username,
            password = request.password,
        ) ?: return RegisterServiceRes.Error.DuplicateUser

        return RegisterServiceRes.Ok(
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
