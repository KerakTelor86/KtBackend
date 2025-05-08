package me.keraktelor.repositories.auth

import me.keraktelor.utilities.exposed.suspendedTransaction
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.and
import java.util.*

class AuthRepositoryImpl : AuthRepository {
    override suspend fun create(
        username: String,
        password: String,
    ): User? = suspendedTransaction {
        if (findByUsername(username) != null) {
            return@suspendedTransaction null
        }

        UserEntity
            .new {
                this.username = username
                this.password = password
            }.toUser()
    }

    override suspend fun findById(id: UUID): User? = suspendedTransaction {
        findActiveUserBy { UsersTable.id eq id }.firstOrNull()?.toUser()
    }

    override suspend fun deactivateById(id: UUID): User? =
        suspendedTransaction {
            findActiveUserBy { UsersTable.id eq id }
                .firstOrNull()
                ?.apply { isActive = false }
                ?.toUser()
        }

    override suspend fun findByUsername(username: String): User? =
        suspendedTransaction {
            findActiveUserBy { UsersTable.username eq username }
                .firstOrNull()
                ?.toUser()
        }

    private fun findActiveUserBy(
        predicate: SqlExpressionBuilder.() -> Op<Boolean>,
    ) = UserEntity.find {
        (UsersTable.isActive eq true) and predicate()
    }
}
