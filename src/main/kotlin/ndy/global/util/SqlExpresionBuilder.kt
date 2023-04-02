package ndy.global.util

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.DeleteStatement
import org.jetbrains.exposed.sql.transactions.TransactionManager

fun and(vararg ops: Op<Boolean>): Op<Boolean> {
    var base = Op.TRUE as Op<Boolean>
    ops.forEach { base = base.and(it) }
    return base
}

fun FieldSet.selectWhere(vararg ops: Op<Boolean>): Query = Query(this, and(*ops))

fun <T : Table> T.deleteWhere(vararg ops: Op<Boolean>) =
    DeleteStatement.where(TransactionManager.current(), this@deleteWhere, and(*ops), false, null, null)
