package me.keraktelor.repositories.auth

import kotlinx.datetime.toJavaLocalDateTime
import me.keraktelor.repositories.auth.User.Companion.toUser
import me.keraktelor.utilities.repository.SqlError
import me.keraktelor.utilities.repository.SqlError.Companion.toSqlError
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class AuthRepositoryImpl : AuthRepository {
    override fun create(user: User): User? = transaction {
        try {
            val entity = UserEntity.new(user.id) {
                username = user.username
                password = user.password
                createdAt = user.createdAt.toJavaLocalDateTime()
            }

            entity.toUser()
        } catch (e: ExposedSQLException) {
            when (e.toSqlError()) {
                SqlError.UniqueViolationError -> null
                else -> throw e
            }
        }
    }

    override fun findById(id: UUID): User? = transaction {
        val entity = UserEntity
            .find {
                (UsersTable.id eq id) and (UsersTable.isActive eq true)
            }.firstOrNull() ?: return@transaction null

        entity.toUser()
    }

    override fun deleteById(id: UUID): User? = transaction {
        val entity = UserEntity.findById(id) ?: return@transaction null

        entity
            .apply {
                isActive = false
            }.toUser()
    }

    override fun findByUsername(username: String): User? = transaction {
        val entity = UserEntity
            .find {
                (UsersTable.username eq username) and
                    (UsersTable.isActive eq true)
            }.firstOrNull() ?: return@transaction null

        entity.toUser()
    }
}
