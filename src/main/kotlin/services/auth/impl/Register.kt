package services.auth.impl

import org.mindrot.jbcrypt.BCrypt
import services.auth.AuthServiceImpl
import services.auth.AuthToken
import services.auth.RegisterServiceReq
import services.auth.RegisterServiceRes

internal suspend fun AuthServiceImpl.registerImpl(
    request: RegisterServiceReq,
): RegisterServiceRes {
    val user = authRepository.create(
        username = request.username,
        password = BCrypt.hashpw(
            request.password,
            BCrypt.gensalt(bcryptRounds),
        ),
    ) ?: return RegisterServiceRes.Error.DuplicateUser

    val id = user.id.toString()

    return RegisterServiceRes.Ok(
        userId = user.id.toString(),
        tokens = AuthToken(
            access = jwt.generateAccess(id),
            refresh = jwt.generateRefresh(id),
        ),
    )
}
