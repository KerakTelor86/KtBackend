package services.auth

import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf

fun Module.authService() {
    singleOf(::AuthServiceImpl) {
        bind<AuthService>()
    }
}

interface AuthService {
    suspend fun register(request: RegisterServiceReq): RegisterServiceRes

    suspend fun login(request: LoginServiceReq): LoginServiceRes

    suspend fun refresh(request: RefreshServiceReq): RefreshServiceRes
}
