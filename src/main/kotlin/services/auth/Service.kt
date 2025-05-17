package services.auth

import org.mindrot.jbcrypt.BCrypt
import repositories.auth.AuthRepository
import repositories.auth.User
import setup.Config

internal class AuthServiceImpl(
    config: Config,
    private val authRepository: AuthRepository,
) : AuthService {
    private val bcryptRounds = config.crypto.bcrypt.rounds

    override suspend fun register(
        request: RegisterServiceReq,
    ): RegisterServiceRes {
        val user = authRepository.create(
            username = request.username,
            password = BCrypt.hashpw(
                request.password,
                BCrypt.gensalt(bcryptRounds),
            ),
        ) ?: return RegisterServiceRes.Error.DuplicateUser

        return RegisterServiceRes.Ok(
            userId = user.id.toString(),
            tokens = generateTokens(user),
        )
    }

    override suspend fun login(request: LoginServiceReq): LoginServiceRes {
        val user = authRepository.findByUsername(request.username)
            ?: return LoginServiceRes.Error.InvalidCredentials

        return when (BCrypt.checkpw(request.password, user.password)) {
            true -> LoginServiceRes.Ok(user.id.toString(), generateTokens(user))
            false -> LoginServiceRes.Error.InvalidCredentials
        }
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
