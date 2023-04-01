package ndy.global.util

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

/**
 * a non-blocking transaction in exposed
 * *
 * namings inspired by `@Transactional - propagation` element of Spring Data
 * see - https://github.com/JetBrains/Exposed/wiki/Transactions#working-with-coroutines
 * see - https://ktor.io/docs/interactive-website-add-persistence.html#queries
 */
// run with new transaction
suspend inline fun <T> requiresNewTransaction(crossinline block: suspend () -> T): T =
    newSuspendedTransaction(Dispatchers.IO) { block() }


// throw error if there is no current transaction
suspend inline fun <T> mandatoryTransaction(crossinline block: suspend () -> T): T {
    TransactionManager.current()
    return block()
}

// if there is current transaction - use it
// else run with new transaction
suspend inline fun <T> requiredTransaction(crossinline block: suspend () -> T): T {
    return if (TransactionManager.currentOrNull() == null) requiresNewTransaction(block)
    else block()
}