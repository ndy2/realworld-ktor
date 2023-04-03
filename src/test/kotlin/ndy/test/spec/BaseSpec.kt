package ndy.test.spec

import io.kotest.core.extensions.Extension
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.property.Arb
import io.kotest.property.PropertyTesting
import io.kotest.property.arbitrary.orNull
import io.kotest.property.resolution.GlobalArbResolver
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.withNullability
import ndy.test.generator.ProfileArbs
import ndy.test.generator.UserArbs

/**
 * Custom Spec that extends FunSpec
 * - can configure extension easily
 * - configuration for property testing
 * - also supports integration-test, transactional-test scopes
 */
@Suppress("UNCHECKED_CAST")
abstract class BaseSpec(
    vararg extensions: Extension = emptyArray(),
    body: BaseSpec.() -> Unit = {}
) : FunSpec(body as FunSpec.() -> Unit), BaseSpecRootScope {

    private val extensions: List<Extension> = extensions.toList()

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
