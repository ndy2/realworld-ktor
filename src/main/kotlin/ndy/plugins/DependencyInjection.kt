package ndy.plugins

import io.ktor.server.application.*
import ndy.domain.user.application.UserService
import ndy.domain.user.domain.UserRepository
import ndy.infra.tables.UserTable
import org.koin.core.module.dsl.bind
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
 * @see <a href=https://insert-koin.io/docs/reference/koin-ktor/ktor/> koin-ktor in koin docs </a>
 */
fun Application.configureDi() {

    install(Koin) {
        slf4jLogger()
        modules(appModule)
    }
}

// Constructor DSL
private val appModule = module {
    singleOf(::UserTable) { bind<UserRepository>() }
    singleOf(::UserService)
}