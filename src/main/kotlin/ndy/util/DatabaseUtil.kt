package ndy.util

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync

/**
 * a non-blocking transaction in exposed
 *
 * see - https://github.com/JetBrains/Exposed/wiki/Transactions#working-with-coroutines
 * see - https://ktor.io/docs/interactive-website-add-persistence.html#queries
 */
suspend inline fun <T> newTransaction(crossinline block: suspend () -> T): T =
    newSuspendedTransaction(Dispatchers.IO) { block() }

suspend inline fun <T> asyncTransaction(crossinline block: suspend () -> T): Deferred<T> {
    return suspendedTransactionAsync(Dispatchers.IO) { block() }
}
