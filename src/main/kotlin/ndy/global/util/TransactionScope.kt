package ndy.global.util

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

/**
 * a non-blocking transaction in exposed
 *
 * see - https://github.com/JetBrains/Exposed/wiki/Transactions#working-with-coroutines
 * see - https://ktor.io/docs/interactive-website-add-persistence.html#queries
 */
suspend inline fun <T> newTransaction(crossinline block: suspend () -> T): T =
    newSuspendedTransaction(Dispatchers.IO) { block() }

suspend inline fun <T> mandatoryTransaction(crossinline block: suspend () -> T): T {
    TransactionManager.current()
    return block()
}
