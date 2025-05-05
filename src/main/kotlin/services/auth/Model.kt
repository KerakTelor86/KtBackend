package me.keraktelor.services.auth

data class AuthToken(
    val access: String,
    val refresh: String,
)

data class RegisterServiceReq(
    val username: String,
    val password: String,
)

sealed class RegisterServiceRes {
    data class Ok(
        val userId: String,
        val tokens: AuthToken,
    ) : RegisterServiceRes()

    sealed class Error : RegisterServiceRes() {
        data object DuplicateUser : Error()
    }
}

data class LoginServiceReq(
    val username: String,
    val password: String,
)

sealed class LoginServiceRes {
    data class Ok(
        val userId: String,
        val tokens: AuthToken,
    ) : LoginServiceRes()

    sealed class Error : LoginServiceRes() {
        data object InvalidCredentials : Error()
    }
}

data class RefreshServiceReq(
    val refreshToken: String,
)

sealed class RefreshServiceRes {
    data class Ok(
        val accessToken: String,
    ) : RefreshServiceRes()
}
