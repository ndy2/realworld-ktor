package ndy.test.extentions

import de.sharpmind.ktor.EnvConfig
import io.kotest.core.listeners.AfterEachListener
import io.kotest.core.listeners.BeforeEachListener
import io.kotest.core.listeners.BeforeSpecListener
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.ktor.server.config.*
import ndy.infra.tables.ProfileTable
import ndy.infra.tables.UserTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DB : BeforeEachListener, AfterEachListener {
    private val database = Database.connect(
        url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
        user = "root",
        driver = "org.h2.Driver",
        password = "",
    )

    override suspend fun beforeEach(testCase: TestCase) = transaction(database) {
        SchemaUtils.create(UserTable.Users)
        SchemaUtils.create(ProfileTable.Profiles)
    }

    override suspend fun afterEach(testCase: TestCase, result: TestResult) = transaction(database) {
        SchemaUtils.drop(ProfileTable.Profiles)
        SchemaUtils.drop(UserTable.Users)
    }
}

object JWT : BeforeSpecListener {

    override suspend fun beforeSpec(spec: Spec) {
        EnvConfig.initConfig(
            MapApplicationConfig(
                "envConfig.default.jwt.domain" to "https://jwt-provider-domain/",
                "envConfig.default.jwt.issuer" to "ndy2",
                "envConfig.default.jwt.audience" to "jwt-audience",
                "envConfig.default.jwt.realm" to "ktor sample app",
                "envConfig.default.jwt.secret" to "secret",
                "envConfig.default.jwt.expires" to "60000",
            )
        )
    }
}