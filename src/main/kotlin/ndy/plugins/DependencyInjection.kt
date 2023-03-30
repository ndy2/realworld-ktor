package ndy.plugins

import io.ktor.server.application.*
import ndy.context.DefaultLoggingContext
import ndy.context.LoggingContext
import ndy.domain.profile.application.ProfileService
import ndy.domain.profile.domain.ProfileRepository
import ndy.domain.user.application.BcryptPasswordService
import ndy.domain.user.application.UserService
import ndy.domain.user.domain.PasswordEncoder
import ndy.domain.user.domain.PasswordVerifier
import ndy.domain.user.domain.UserRepository
import ndy.infra.tables.ProfileTable
import ndy.infra.tables.UserTable
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

/**
 * Configure DI with Koin!
 * there is no DI feature in Ktor, Let's use Koin!
 *
 * Koin has no auto-configuration feature as in Spring Boot
 * register module in below code
 *
 * see https://insert-koin.io/docs/reference/koin-ktor/ktor/
 */
fun Application.configureDi() {

    install(Koin) {
        slf4jLogger()
        modules(appModule)
    }
}

// Constructor DSL
private val appModule = module {
    single<LoggingContext> { DefaultLoggingContext } // should be added for context(LoggingContext)

    // user domain
    single<UserRepository> { UserTable }
    single<PasswordEncoder> { BcryptPasswordService }
    single<PasswordVerifier> { BcryptPasswordService }
    singleOf(::UserService)

    // profile domain
    single<ProfileRepository> { ProfileTable }
    singleOf(::ProfileService)
}
