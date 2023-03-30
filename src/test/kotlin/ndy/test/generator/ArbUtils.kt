package ndy.test.generator

import io.kotest.property.Arb
import io.kotest.property.arbitrary.bind
import io.kotest.property.resolution.GlobalArbResolver
import kotlin.reflect.typeOf

inline fun <reified T> registerArb(vararg arbArgs: Arb<*>) {
    val arb = Arb.bind(arbArgs.toList()) { args ->
        val constructorArgs = args.toTypedArray()
        T::class.constructors
            .filter { noException { it.call(*constructorArgs) } }
            .map { it.call(*constructorArgs) }
            .first()
    }
    GlobalArbResolver.register(typeOf<T>(), arb)
}

fun noException(block: () -> Unit): Boolean {
    try {
        block()
    } catch (e: Exception) {
        return false
    }
    return true
}