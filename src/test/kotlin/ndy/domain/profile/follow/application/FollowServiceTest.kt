package ndy.domain.profile.follow.application

import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.assume
import io.kotest.property.checkAll
import ndy.context.AuthenticatedUserContext
import ndy.domain.user.domain.UserId
import ndy.infra.tables.FollowTable
import ndy.test.extentions.DB
import ndy.test.spec.BaseSpec
import ndy.util.newTransaction
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

    context("check follow") {
        test("save n, delete m and check exists for all saved entries") {
            checkAll(Arb.int(4, 10), Arb.int(0, 5)) { n, m ->
                // setup
                assume(n >= m)
                val savePair = buildList(n) {
                    repeat(n) {
                        val followerId = UserId(Random.nextULong())
                        val followeeId = UserId(Random.nextULong())
                        add(followerId to followeeId)
                    }
                }
                val deleteList = savePair.slice(0..<m)

                // action
                newTransaction {
                    savePair.forEach { with(userIdContext(it.first)) { sut.follow(it.second) } }
                    deleteList.forEach { with(userIdContext(it.first)) { sut.unfollow(it.second) } }
                }

                // assert
                // @formatter:off
            newTransaction {
                (0..<m).map { savePair[it] }.forEach { with(userIdContext(it.first)) {sut.checkFollow(it.second) shouldBe false }}
                (m..<n).map { savePair[it] }.forEach { with(userIdContext(it.first)) {sut.checkFollow(it.second) shouldBe true }}
            }
            // @formatter:on
            }
        }
    }
})

fun userIdContext(userId: UserId) =
    object : AuthenticatedUserContext {
        override val userId = userId
        override val userIdNullable = userId
    }
