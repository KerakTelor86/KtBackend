package services.auth.impl

import org.mindrot.jbcrypt.BCrypt
import services.auth.AuthServiceImpl
import services.auth.AuthToken
import services.auth.LoginServiceReq
import services.auth.LoginServiceRes

internal suspend fun AuthServiceImpl.loginImpl(
    request: LoginServiceReq,
): LoginServiceRes {
    val user = authRepository.findByUsername(request.username)
        ?: return LoginServiceRes.Error.InvalidCredentials

    val id = user.id.toString()

    return when (BCrypt.checkpw(request.password, user.password)) {
        true -> LoginServiceRes.Ok(
            user.id.toString(),
            AuthToken(
                access = jwt.generateAccess(id),
                refresh = jwt.generateRefresh(id),
            ),
        )

        false -> LoginServiceRes.Error.InvalidCredentials
    }
}
