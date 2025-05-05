package me.keraktelor.utilities.repository

import org.jetbrains.exposed.exceptions.ExposedSQLException

// https://www.postgresql.org/docs/10/errcodes-appendix.html
sealed class SqlError {
    companion object {
        fun ExposedSQLException.toSqlError(): SqlError = when (this.errorCode) {
            23502 -> NotNullViolationError
            23503 -> ForeignKeyViolationError
            23505 -> UniqueViolationError
            else -> UnhandledSqlError(this)
        }
    }

    data object NotNullViolationError : SqlError()

    data object ForeignKeyViolationError : SqlError()

    data object UniqueViolationError : SqlError()

    data class UnhandledSqlError(
        val exception: ExposedSQLException,
    ) : SqlError()
}
