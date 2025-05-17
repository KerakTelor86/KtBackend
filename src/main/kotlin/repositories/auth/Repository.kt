package repositories.auth

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.and
import utilities.exposed.suspendedTransaction
import java.time.LocalDateTime
import java.util.*

class AuthRepositoryImpl(
    private val db: Database,
) : AuthRepository {
    override suspend fun create(
        username: String,
        password: String,
    ): User? = suspendedTransaction(db = db) {
        if (findByUsername(username) != null) {
            return@suspendedTransaction null
        }

        UserEntity
            .new {
                this.username = username
                this.password = password
                this.createdAt = LocalDateTime.now()
            }.toUser()
    }

    override suspend fun findById(id: UUID): User? =
        suspendedTransaction(db = db) {
            findActiveUserBy { UsersTable.id eq id }.firstOrNull()?.toUser()
        }

    override suspend fun deactivateById(id: UUID): User? =
        suspendedTransaction(db = db) {
            findActiveUserBy { UsersTable.id eq id }
                .firstOrNull()
                ?.apply { isActive = false }
                ?.toUser()
        }

    override suspend fun findByUsername(username: String): User? =
        suspendedTransaction(db = db) {
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
