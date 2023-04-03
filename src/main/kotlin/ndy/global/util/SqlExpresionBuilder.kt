package ndy.global.util

import org.jetbrains.exposed.sql.FieldSet
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.statements.DeleteStatement
import org.jetbrains.exposed.sql.transactions.TransactionManager

/**
 * apply and for all provided ops
 */
fun and(vararg ops: Op<Boolean>): Op<Boolean> {
    var base = Op.TRUE as Op<Boolean>
    ops.forEach { base = base.and(it) }
    return base
}

/**
 * create Query from this FieldSet with apply `and` for all provided ops
 */
fun FieldSet.selectWhere(vararg ops: Op<Boolean>): Query = Query(this, and(*ops))

/**
 * execute a deleteStatement from this Table with apply `and` for all provided ops
 */
fun <T : Table> T.deleteWhere(vararg ops: Op<Boolean>) =
        DeleteStatement.where(TransactionManager.current(), this@deleteWhere, and(*ops), false, null, null)
