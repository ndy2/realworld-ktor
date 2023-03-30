package ndy.infra.tables

import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.assume
import io.kotest.property.checkAll
import ndy.domain.user.domain.UserId
import ndy.test.extentions.DB
import ndy.test.spec.BaseSpec
import ndy.util.newTransaction
import kotlin.random.Random
import kotlin.random.nextULong

@OptIn(ExperimentalStdlibApi::class)
class FollowTableTest : BaseSpec(DB, body = {

    val sut = FollowTable

    test("save n, delete m and check exists for all saved entries") {
        checkAll(Arb.int(3, 10), Arb.int(0, 5)) { n, m ->
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
                savePair.forEach { sut.save(it.first, it.second) }
                deleteList.forEach { sut.delete(it.first, it.second) }
            }

            // assert
            newTransaction {
                (0..<m).map { savePair[it] }.forEach { sut.exists(it.first, it.second) shouldBe false }
                (m..<n).map { savePair[it] }.forEach { sut.exists(it.first, it.second) shouldBe true }
            }
        }
    }
})