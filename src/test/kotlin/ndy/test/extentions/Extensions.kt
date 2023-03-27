package ndy.test.extentions

import de.sharpmind.ktor.EnvConfig
import io.kotest.core.listeners.AfterSpecListener
import io.kotest.core.listeners.BeforeSpecListener
import io.kotest.core.spec.Spec
import io.kotest.koin.KoinExtension
import io.kotest.koin.KoinLifecycleMode
import io.ktor.server.config.*
import ndy.domain.user.application.BcryptPasswordService
import ndy.domain.user.application.UserService
import ndy.domain.user.domain.PasswordEncoder
import ndy.domain.user.domain.PasswordVerifier
import ndy.domain.user.domain.UserRepository
import ndy.infra.tables.UserTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.dsl.module

val DI = KoinExtension(
    module = module {
        single<UserRepository> { UserTable() }
        single<PasswordEncoder> { BcryptPasswordService }
        single<PasswordVerifier> { BcryptPasswordService }
        single { UserService(get(), get(), get()) }
    },
    mode = KoinLifecycleMode.Test
)

object DB : BeforeSpecListener, AfterSpecListener {
    private val database = Database.connect(
        url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
        user = "root",
        driver = "org.h2.Driver",
        password = "",
    )

    override suspend fun beforeSpec(spec: Spec) = transaction(database) {
        SchemaUtils.create(UserTable.Users)
    }

    override suspend fun afterSpec(spec: Spec) = transaction(database) {
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