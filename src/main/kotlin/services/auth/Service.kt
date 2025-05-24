package services.auth

import repositories.auth.AuthRepository
import services.auth.impl.decodeImpl
import services.auth.impl.loginImpl
import services.auth.impl.refreshImpl
import services.auth.impl.registerImpl
import setup.Config

internal class AuthServiceImpl(
    config: Config,
    internal val authRepository: AuthRepository,
) : AuthService {
    internal val bcryptRounds = config.crypto.bcrypt.rounds
    internal val jwt = JwtManager(config)

    init {
        require(bcryptRounds in 10..15) {
            "BCrypt rounds must be between 10 and 15"
        }
    }

    override suspend fun register(request: RegisterServiceReq) =
        registerImpl(request)

    override suspend fun login(request: LoginServiceReq) =
        loginImpl(request)

    override suspend fun refresh(request: RefreshServiceReq) =
        refreshImpl(request)

    override suspend fun decode(request: DecodeTokenServiceReq) =
        decodeImpl(request)
}
