package ndy.domain.article.application

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.collections.shouldBeIn
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.Arb
import io.kotest.property.RandomSource
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.triple
import io.kotest.property.checkAll
import ndy.domain.article.comment.application.CommentService
import ndy.domain.article.favorite.application.FavoriteService
import ndy.domain.profile.application.ProfileService
import ndy.domain.profile.follow.application.FollowService
import ndy.domain.tag.application.TagService
import ndy.domain.user.application.BcryptPasswordService
import ndy.domain.user.application.UserService
import ndy.global.util.transactional
import ndy.infra.tables.ArticleTable
import ndy.infra.tables.CommentTable
import ndy.infra.tables.FavoriteTable
import ndy.infra.tables.FollowTable
import ndy.infra.tables.ProfileTable
import ndy.infra.tables.TagTable
import ndy.infra.tables.UserTable
import ndy.test.context.withNoToken
import ndy.test.context.withToken
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
        val userAUsernames = mutableListOf<String>()
        var articleCount = 0
        checkAll(Arb.list(Arb.triple(emailValueArb, passwordValueArb, usernameValueArb), 4..4)) { fixture ->
            // setup
            val (userA, userB, userC, userD) = fixture
            fixture.forEach {
                userService.register(email = it.first, password = it.second, username = it.third)
                println(it.third)
            }

            userAUsernames.add(userA.third)
            val token = transactional { userService.login(userA.first, userA.second).token!! }

            transactionalTest("userA create an article") {
                val title = titleValueArb.sample(RandomSource.default()).value
                val description = descriptionValueArb.sample(RandomSource.default()).value
                val body = bodyValueArb.sample(RandomSource.default()).value
                val tagList = tagValueListArb.sample(RandomSource.default()).value

                // action
                articleCount++
                val result = withToken(token) { sut.create(title, description, body, tagList) }

                // assert
                assertSoftly(result) {
                    result.slug shouldNotBe null
                    it.title shouldBe title
                    it.description shouldBe description
                    it.body shouldBe body
                    it.tagList shouldBe tagList
                    it.createdAt shouldNotBe null
                    it.updatedAt shouldNotBe null
                    it.favorited shouldBe false
                    it.favoritesCount shouldBe 0
                    assertSoftly(result.author) { author ->
                        author.username shouldBe userA.third
                        author.bio shouldBe null
                        author.image shouldBe null
                        author.following shouldBe false
                    }
                }
            }
        }

        transactionalTest("find all created article by userA") {
            // action
            val result = withNoToken { sut.searchByCond(ArticleSearchCond(null, null, null, 20, 0)) }

            // assert
            result shouldHaveSize articleCount
            result.forEach { article ->
                assertSoftly(article) {
                    it.author.username shouldBeIn userAUsernames
                }
            }
        }
    }
})

