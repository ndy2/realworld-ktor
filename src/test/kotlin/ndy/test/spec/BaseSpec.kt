package ndy.test.spec

import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.property.Arb
import io.kotest.property.PropertyTesting
import io.kotest.property.resolution.GlobalArbResolver
import io.ktor.server.testing.*
import ndy.plugins.configureDatabases
import ndy.test.generator.UserArbs
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberProperties

abstract class BaseSpec(body: FunSpec.() -> Unit = {}) : FunSpec(body) {

    override suspend fun beforeSpec(spec: Spec) {
        // set default iteration count - 10
        PropertyTesting.defaultIterationCount = 10

        // register custom arb
        registerCustomArbs(UserArbs::class)

        // configure application
        testApplication { application { configureDatabases() } }
    }

    private fun registerCustomArbs(kClass: KClass<*>) {
        kClass.declaredMemberProperties
            .filterNot { it.returnType.arguments[0].type.toString().contains("kotlin") }
            .forEach {
                GlobalArbResolver.register(
                    it.returnType.arguments[0].type!!,
                    it.getter.call(kClass.objectInstance!!) as Arb<*>
                )
            }
    }
}