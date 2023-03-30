package ndy.test.spec

import io.kotest.core.extensions.Extension
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.property.Arb
import io.kotest.property.PropertyTesting
import io.kotest.property.arbitrary.orNull
import io.kotest.property.resolution.GlobalArbResolver
import ndy.test.generator.ProfileArbs
import ndy.test.generator.UserArbs
import org.koin.test.KoinTest
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.withNullability

/**
 * Custom Spec with FunSpec
 * - can configure extension easily
 * - configuration for property testing
 */
abstract class BaseSpec(
    vararg extensions: Extension = emptyArray(),
    body: FunSpec.() -> Unit = {}
) : FunSpec(body), KoinTest {

    private val extensions: List<Extension> = extensions.toList()

    // see https://kotest.io/docs/extensions/koin.html for detail
    override fun extensions() = extensions

    // common hooks - configure property testing
    override suspend fun beforeSpec(spec: Spec) {
        PropertyTesting.defaultIterationCount = 5
        registerCustomArbs(UserArbs::class)
        registerCustomArbs(ProfileArbs::class)
    }

    private fun registerCustomArbs(kClass: KClass<*>) {
        kClass.declaredMemberProperties
            .filterNot { it.returnType.arguments[0].type.toString().contains("kotlin") }
            .forEach {
                val type = it.returnType.arguments[0].type!!
                val arb = it.getter.call(kClass.objectInstance!!) as Arb<*>
                GlobalArbResolver.register(type, arb)

                val nullableType = type.withNullability(true)
                val nullableArb = arb.orNull()
                GlobalArbResolver.register(nullableType, nullableArb)
            }
    }
}