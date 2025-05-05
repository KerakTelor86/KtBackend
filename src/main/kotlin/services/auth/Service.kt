package me.keraktelor.services.auth

internal class AuthServiceImpl : AuthService {
    override suspend fun register(
        request: RegisterServiceReq,
    ): RegisterServiceRes {
        TODO("Not yet implemented")
    }

    override suspend fun login(request: LoginServiceReq): LoginServiceRes {
        TODO("Not yet implemented")
    }

    override suspend fun refresh(
        request: RefreshServiceReq,
    ): RefreshServiceRes {
        TODO("Not yet implemented")
    }
}
