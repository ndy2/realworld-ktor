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

// Classic DSL
val appModuleClassic = module {
    single<UserRepository> { UserTable() }
    single { UserService(get()) }
}