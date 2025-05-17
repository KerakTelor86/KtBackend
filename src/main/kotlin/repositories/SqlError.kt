package repositories

import org.jetbrains.exposed.exceptions.ExposedSQLException

// https://www.postgresql.org/docs/10/errcodes-appendix.html
@Suppress("unused")
sealed class SqlError(
    val exception: ExposedSQLException,
) {
    companion object {
        fun ExposedSQLException.toSqlError(): SqlError = when (this.errorCode) {
            23502 -> NotNullViolationError(this)
            23503 -> ForeignKeyViolationError(this)
            23505 -> UniqueViolationError(this)
            else -> UnhandledSqlError(this)
        }
    }

    class NotNullViolationError(
        e: ExposedSQLException,
    ) : SqlError(e)

    class ForeignKeyViolationError(
        e: ExposedSQLException,
    ) : SqlError(e)

    class UniqueViolationError(
        e: ExposedSQLException,
    ) : SqlError(e)

    class UnhandledSqlError(
        e: ExposedSQLException,
    ) : SqlError(e)
}
