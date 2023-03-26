package ndy.test.spec

import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.property.PropertyTesting
import io.kotest.property.resolution.GlobalArbResolver
import io.ktor.server.testing.*
import ndy.domain.user.domain.Email
import ndy.domain.user.domain.Password
import ndy.domain.user.domain.Username
import ndy.plugins.configureDatabases
import ndy.test.generator.UserArbs

abstract class TableTestSpec(body: FunSpec.() -> Unit = {}) : FunSpec(body) {

    override suspend fun beforeSpec(spec: Spec) {
        PropertyTesting.defaultIterationCount = 10
        GlobalArbResolver.register<Username>(UserArbs.UsernameArb)
        GlobalArbResolver.register<Email>(UserArbs.EmailArb)
        GlobalArbResolver.register<Password>(UserArbs.PasswordArb)

        testApplication { application { configureDatabases() } }
    }
}