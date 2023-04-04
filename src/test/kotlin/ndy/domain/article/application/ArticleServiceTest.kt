package ndy.domain.article.application

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.kotest.property.Arb
import io.kotest.property.RandomSource
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.triple
import io.kotest.property.checkAll
import ndy.domain.article.comment.application.CommentService
import ndy.domain.article.favorite.application.FavoriteService
import ndy.domain.profile.application.ProfileService
import ndy.domain.profile.domain.ProfileId
import ndy.domain.profile.follow.application.FollowService
import ndy.domain.tag.application.TagService
import ndy.domain.user.application.BcryptPasswordService
import ndy.domain.user.application.UserService
import ndy.domain.user.domain.UserId
import ndy.global.context.AuthenticatedUserContext
import ndy.global.util.transactional
import ndy.infra.tables.ArticleTable
import ndy.infra.tables.CommentTable
import ndy.infra.tables.FavoriteTable
import ndy.infra.tables.FollowTable
import ndy.infra.tables.ProfileTable
import ndy.infra.tables.TagTable
import ndy.infra.tables.UserTable
import ndy.test.extentions.Db
import ndy.test.extentions.Jwt
import ndy.test.generator.ArticleArbs.bodyValueArb
import ndy.test.generator.ArticleArbs.descriptionValueArb
import ndy.test.generator.ArticleArbs.tagValueListArb
import ndy.test.generator.ArticleArbs.titleValueArb
import ndy.test.generator.ProfileArbs.usernameValueArb
import ndy.test.generator.UserArbs.emailValueArb
import ndy.test.generator.UserArbs.passwordValueArb
import ndy.test.spec.BaseSpec

class ArticleServiceTest : BaseSpec(Db, Jwt, body = {

    val profileService = ProfileService(ProfileTable, FollowService(FollowTable))
    val userService = UserService(
            UserTable,
            profileService,
            BcryptPasswordService,
            BcryptPasswordService
    )
    val sut = ArticleService(
            ArticleTable,
            TagService(TagTable),
            profileService,
            FollowService(FollowTable),
            CommentService(CommentTable),
            FavoriteService(FavoriteTable)
    )

    describe("with four registered user") {
        checkAll(Arb.list(Arb.triple(emailValueArb, passwordValueArb, usernameValueArb), 4..4)) { fixture ->
            val (userA, userB, userC, userD) = fixture
            println("userA = ${userA}")
            fixture.forEach { userService.register(email = it.first, password = it.second, username = it.third) }

            val token = transactional { userService.login(userA.first, userA.second).token!! }

            println("token = ${token}")

            val claims = JWT
                    .require(Algorithm.HMAC256("secret"))
                    .withAudience("jwt-audience")
                    .withIssuer("ndy2")
                    .build()
                    .verify(token)
                    .claims

            val userId = claims["user_id"]!!.asLong().toULong()
            val profileId = claims["user_id"]!!.asLong().toULong()

            val context = object : AuthenticatedUserContext {
                override val authenticated = true
                override val userId = UserId(userId)
                override val profileId = ProfileId(profileId)
            }



            transactionalTest("userA create an article") {
                val title = titleValueArb.sample(RandomSource.default()).value
                val description = descriptionValueArb.sample(RandomSource.default()).value
                val body = bodyValueArb.sample(RandomSource.default()).value
                val tags = tagValueListArb.sample(RandomSource.default()).value

                with(context) { sut.create(title, description, body, tags) }

            }
        }
    }

})

