package ndy.global.util

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

/**
 * a non-blocking transaction in exposed
 *
 * - namings inspired by `@Transactional - propagation` element of Spring Data
 * - reference - https://github.com/JetBrains/Exposed/wiki/Transactions#working-with-coroutines
 * - reference - https://ktor.io/docs/interactive-website-add-persistence.html#queries
 */
suspend inline fun <T> transactional(
    propagation: Propagation = Propagation.REQUIRED,
    crossinline block: suspend () -> T
): T {
    return when (propagation) {
        Propagation.REQUIRED -> requiredTransaction(block)
        Propagation.REQUIRES_NEW -> requiresNewTransaction(block)
        Propagation.MANDATORY -> mandatoryTransaction(block)
    }
}

enum class Propagation {
    REQUIRED,
    REQUIRES_NEW,
    MANDATORY,
}

// run with new transaction
suspend inline fun <T> requiresNewTransaction(crossinline block: suspend () -> T): T {
    return newSuspendedTransaction(Dispatchers.IO) { block() }
}


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

