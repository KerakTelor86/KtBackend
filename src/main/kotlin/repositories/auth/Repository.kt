package me.keraktelor.repositories.auth

import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class AuthRepositoryImpl : AuthRepository {
    override fun create(
        username: String,
        password: String,
    ): User? = transaction {
        if (findByUsername(username) != null) {
            return@transaction null
        }

        UserEntity
            .new {
                this.username = username
                this.password = password
            }.toUser()
    }

    override fun findById(id: UUID): User? =
        transaction {
            findActiveUserBy { UsersTable.id eq id }.firstOrNull()?.toUser()
        }

    override fun deactivateById(id: UUID): User? =
        transaction {
            findActiveUserBy { UsersTable.id eq id }
                .firstOrNull()
                ?.apply { isActive = false }
                ?.toUser()
        }

    override fun findByUsername(username: String): User? =
        transaction {
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
