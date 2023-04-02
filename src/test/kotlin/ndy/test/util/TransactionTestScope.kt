package ndy.test.util

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestScope
import ndy.global.util.requiresNewTransaction
import org.jetbrains.exposed.sql.transactions.TransactionManager

fun commit() = TransactionManager.current().commit()

fun FunSpec.transactionTest(name: String, block: suspend TestScope.() -> Unit) {
    test(name) {
        requiresNewTransaction {
            block()
        }
    }
}
