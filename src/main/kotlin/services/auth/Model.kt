package services.auth

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

    sealed class Error : RefreshServiceRes() {
        data object RefreshTokenExpired : Error()

        data object RefreshTokenInvalid : Error()
    }
}

data class DecodeTokenServiceReq(
    val accessToken: String,
)

sealed class DecodeTokenServiceRes {
    data class Ok(
        val userId: String,
    ) : DecodeTokenServiceRes()

    sealed class Error : DecodeTokenServiceRes() {
        data object AccessTokenExpired : Error()

        data object AccessTokenInvalid : Error()
    }
}
