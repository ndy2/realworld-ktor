package ndy.test.extentions

import de.sharpmind.ktor.EnvConfig
import io.kotest.core.listeners.BeforeContainerListener
import io.kotest.core.test.TestCase
import io.ktor.server.config.MapApplicationConfig

object Jwt : BeforeContainerListener {
    override suspend fun beforeContainer(testCase: TestCase) {
        EnvConfig.initConfig(
                MapApplicationConfig(
                        "envConfig.default.jwt.domain" to "https://jwt-provider-domain/",
                        "envConfig.default.jwt.issuer" to "ndy2",
                        "envConfig.default.jwt.audience" to "jwt-audience",
                        "envConfig.default.jwt.realm" to "ktor sample app",
                        "envConfig.default.jwt.secret" to "secret",
                        "envConfig.default.jwt.expires" to "60000"
                )
        )
    }
}
