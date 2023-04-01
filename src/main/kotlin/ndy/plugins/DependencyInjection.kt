package ndy.plugins

import io.ktor.server.application.*
import ndy.domain.article.application.ArticleService
import ndy.domain.article.comment.application.CommentService
import ndy.domain.article.comment.domain.CommentRepository
import ndy.domain.article.domain.ArticleRepository
import ndy.domain.article.favorite.application.FavoriteService
import ndy.domain.article.favorite.domain.FavoriteRepository
import ndy.domain.profile.application.ProfileService
import ndy.domain.profile.domain.ProfileRepository
import ndy.domain.profile.follow.application.FollowService
import ndy.domain.profile.follow.domain.FollowRepository
import ndy.domain.tag.application.TagService
import ndy.domain.tag.domain.TagRepository
import ndy.domain.user.application.BcryptPasswordService
import ndy.domain.user.application.UserService
import ndy.domain.user.domain.PasswordEncoder
import ndy.domain.user.domain.PasswordVerifier
import ndy.domain.user.domain.UserRepository
import ndy.global.context.applicationLoggingContext
import ndy.infra.tables.*
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

/**
 * Configure DI with Koin!
 * there is no DI feature in Ktor, Let's use Koin!
 * *
 * Koin has no auto-configuration feature as in Spring Boot
 * register module in below code
 * *
 * see https://insert-koin.io/docs/reference/koin-ktor/ktor/
 */
fun Application.configureDi() {

    install(Koin) {
        slf4jLogger()
        modules(module {
            // logging context - used by koin
            singleOf(::applicationLoggingContext)

            // user domain
            single<UserRepository> { UserTable }
            single<PasswordEncoder> { BcryptPasswordService }
            single<PasswordVerifier> { BcryptPasswordService }
            singleOf(::UserService)

            // profile domain
            single<ProfileRepository> { ProfileTable }
            singleOf(::ProfileService)

            // follow
            single<FollowRepository> { FollowTable }
            singleOf(::FollowService)

            // article domain
            single<ArticleRepository> { ArticleTable }
            singleOf(::ArticleService)

            // comment
            single<CommentRepository> { CommentTable }
            singleOf(::CommentService)

            // favorite
            single<FavoriteRepository> { FavoriteTable }
            singleOf(::FavoriteService)

            // tag domain
            single<TagRepository> { TagTable }
            singleOf(::TagService)
        })
    }
}
