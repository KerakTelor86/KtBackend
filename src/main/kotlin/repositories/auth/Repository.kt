package me.keraktelor.repositories.auth

import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.*

class AuthRepositoryImpl : AuthRepository {
    override suspend fun create(
        username: String,
        password: String,
    ): User? = newSuspendedTransaction {
        if (findByUsername(username) != null) {
            return@newSuspendedTransaction null
        }

        UserEntity
            .new {
                this.username = username
                this.password = password
            }.toUser()
    }

    override suspend fun findById(id: UUID): User? =
        newSuspendedTransaction {
            findActiveUserBy { UsersTable.id eq id }.firstOrNull()?.toUser()
        }

    override suspend fun deactivateById(id: UUID): User? =
        newSuspendedTransaction {
            findActiveUserBy { UsersTable.id eq id }
                .firstOrNull()
                ?.apply { isActive = false }
                ?.toUser()
        }

    override suspend fun findByUsername(username: String): User? =
        newSuspendedTransaction {
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
