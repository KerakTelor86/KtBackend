package repositories

import org.koin.dsl.module
import repositories.auth.authRepository

val repositoryModule = module {
    authRepository()
}
