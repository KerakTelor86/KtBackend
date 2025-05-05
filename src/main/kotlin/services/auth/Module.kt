package me.keraktelor.services.auth

import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf

fun Module.authService() {
    singleOf<AuthService>(::AuthServiceImpl)
}

interface AuthService {
    suspend fun register(request: RegisterServiceReq): RegisterServiceRes

    suspend fun login(request: LoginServiceReq): LoginServiceRes

    suspend fun refresh(request: RefreshServiceReq): RefreshServiceRes
}
