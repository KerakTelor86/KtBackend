package me.keraktelor.services.auth

data class RegisterServiceReq(
    val username: String,
    val password: String,
)

sealed class RegisterServiceRes {
    data class Ok(
        val userId: String,
        val accessToken: String,
    ) : RegisterServiceRes()

    sealed class Error : RegisterServiceRes()
}

data class LoginServiceReq(
    val username: String,
    val password: String,
)

sealed class LoginServiceRes {
    data class Ok(
        val userId: String,
        val accessToken: String,
    ) : LoginServiceRes()

    sealed class Error : LoginServiceRes() {
        data object InvalidCredentials : Error()
    }
}
