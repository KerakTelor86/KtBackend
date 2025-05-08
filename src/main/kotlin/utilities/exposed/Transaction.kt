package me.keraktelor.utilities.exposed

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.experimental.withSuspendTransaction
import kotlin.coroutines.CoroutineContext

suspend fun <T> suspendedTransaction(
    context: CoroutineContext? = null,
    db: Database? = null,
    transactionIsolation: Int? = null,
    statement: suspend Transaction.() -> T,
): T {
    TransactionManager.currentOrNull()?.let { transaction ->
        if (isMatchingTransaction(transaction, db, transactionIsolation)) {
            return transaction.withSuspendTransaction(
                context = context,
                statement = statement,
            )
        }
    }
    return newSuspendedTransaction(
        context = context,
        db = db,
        transactionIsolation = transactionIsolation,
        statement = statement,
    )
}

private fun isMatchingTransaction(
    existing: Transaction,
    db: Database?,
    transactionIsolation: Int?,
): Boolean {
    if (db != null && db != existing.db) {
        return false
    }
    if (transactionIsolation != null &&
        transactionIsolation != existing.transactionIsolation
    ) {
        return false
    }
    return true
}
