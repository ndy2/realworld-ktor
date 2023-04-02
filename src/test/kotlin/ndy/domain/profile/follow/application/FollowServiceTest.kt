package ndy.domain.profile.follow.application

import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.assume
import io.kotest.property.checkAll
import ndy.domain.profile.domain.ProfileId
import ndy.domain.user.domain.UserId
import ndy.global.context.AuthenticatedUserContext
import ndy.infra.tables.FollowTable
import ndy.test.extentions.DB
import ndy.test.spec.BaseSpec
import kotlin.random.Random
import kotlin.random.nextULong

@OptIn(ExperimentalStdlibApi::class)
class FollowServiceTest : BaseSpec(DB, body = {

    val sut = FollowService(FollowTable)

    xcontext("follow user") {
        test("follow success!") {
        }

        test("self follow is not allowed") {
        }

        test("duplicated follow is not allowed") {
        }
    }

    xcontext("unfollow user") {
        test("self unfollow is not allowed") {
        }
        test("follow success!") {
        }
    }

    transactionalTest("save n, delete m and check exists for all saved entries") {
        checkAll(Arb.int(4, 10), Arb.int(0, 5)) { n, m ->
            // setup
            assume(n >= m)
            val savePair = buildList(n) {
                repeat(n) {
                    val followerId = ProfileId(Random.nextULong())
                    val followeeId = ProfileId(Random.nextULong())
                    add(followerId to followeeId)
                }
            }
            val deleteList = savePair.slice(0..<m)

            // action
            savePair.forEach { with(userContext(it.first)) { sut.follow(it.second) } }
            deleteList.forEach { with(userContext(it.first)) { sut.unfollow(it.second) } }

            // assert
            // @formatter:off
            (0..<m).map { savePair[it] }.forEach { with(userContext(it.first)) {sut.isFollowing(it.second) shouldBe false }}
            (m..<n).map { savePair[it] }.forEach { with(userContext(it.first)) {sut.isFollowing(it.second) shouldBe true }}
            // @formatter:on
        }
    }
})

fun userContext(profileId: ProfileId) =
    object : AuthenticatedUserContext {
        override val authenticated = false
        override val userId = UserId(0u)
        override val profileId = profileId
    }
