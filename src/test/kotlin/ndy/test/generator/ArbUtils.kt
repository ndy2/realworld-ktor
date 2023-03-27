package ndy.test.generator

import io.kotest.property.Arb
import io.kotest.property.RandomSource
import io.kotest.property.arbitrary.ArbitraryBuilder
import io.kotest.property.resolution.GlobalArbResolver
import kotlin.reflect.typeOf

inline fun <reified T> registerArb(vararg arbArgs: Arb<*>) {
    val arb = createArb<T>(*arbArgs)
    GlobalArbResolver.register(typeOf<T>(), arb)
}

/**
 * create arb with sample function
 */
inline fun <reified T> createArb(crossinline sampleFunction: (RandomSource) -> T): Arb<T> {
    return ArbitraryBuilder.create { rs ->
        sampleFunction(rs)
    }.build()
}

/**
 * create arb by combination of given arbs by calling constructor of T
 */
inline fun <reified T> createArb(vararg arbArgs: Arb<*>): Arb<T> {
    return createArb { rs ->
        try {
            val constructorArguments = arbArgs.map { it.sample(rs).value }.toTypedArray()
            ArbitraryBuilder.create {
                T::class.constructors
                    .filter { noException { it.call(*constructorArguments) } }
                    .map { it.call(*constructorArguments) }
                    .first()

            }.build().sample(rs).value
        } catch (e: Exception) {
            throw IllegalArgumentException("bad constructor argument for type T: ${T::class.simpleName}")
        }
    }
}

fun noException(block: () -> Unit) = !exception(block)

fun exception(block: () -> Unit): Boolean {
    try {
        block()
    } catch (e: Exception) {
        return true
    }
    return false
}