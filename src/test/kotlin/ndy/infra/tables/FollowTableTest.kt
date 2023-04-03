package ndy.infra.tables

import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.pair
import io.kotest.property.assume
import io.kotest.property.checkAll
import ndy.domain.profile.domain.ProfileId
import ndy.test.extentions.DB
import ndy.test.generator.ProfileArbs.usernameArb
import ndy.test.generator.UserArbs.userArb
import ndy.test.spec.BaseSpec
import ndy.test.util.assumeNotDuplicated
import kotlin.random.Random
import kotlin.random.nextULong

class FollowTableTest : BaseSpec(DB, body = {

    val sut = FollowTable
    val userTable = UserTable
    val profileTable = ProfileTable

    transactionalTest("save n, delete m and check exists for all saved entries") {
        checkAll(Arb.int(5, 10), Arb.int(0, 5)) { n, m ->
            // setup
            assume(n >= m)
            val savePair = buildList(n) {
                repeat(n) {
                    val followerId = ProfileId(Random.nextULong())
                    val followeeId = ProfileId(Random.nextULong())
                    add(followerId to followeeId)
                }
            }
            val deleteList = savePair.slice(0 until m)

            // action
            savePair.forEach { sut.save(it.first, it.second) }
            deleteList.forEach { sut.delete(it.first, it.second) }

            // assert
            (0 until m).map { savePair[it] }.forEach { sut.exists(it.first, it.second) shouldBe false }
            (m until n).map { savePair[it] }.forEach { sut.exists(it.first, it.second) shouldBe true }
        }
    }

    // create n users and make following flag matrix
    //    0  1  2
    // 0  x  x  o
    // 1  o  x  o
    // 2  o  o  x
    // and assert each row
    transactionalTest("get following flag list") {
        checkAll(
            Arb.list(Arb.pair(userArb, usernameArb), 2..5)
        ) { fixtures ->
            // setup
            val profileIds = fixtures.map {
                val (user, username) = it
                with(profileTable) { assumeNotDuplicated(username.value) }
                val savedUser = userTable.save(user)
                val savedProfile = profileTable.save(savedUser.id, username)
                savedProfile.id
            }

            val followingMatrix = mutableListOf<MutableList<Boolean>>()
            for (followerId in profileIds) {
                val followingList = mutableListOf<Boolean>()
                for (followeeId in profileIds) {
                    if (followerId != followeeId && Random.nextInt() % 2 == 0) {
                        followingList.add(true)
                        sut.save(followerId, followeeId)
                    } else {
                        followingList.add(false)
                    }
                }
                followingMatrix.add(followingList)
            }

            for ((idx, followerId) in profileIds.withIndex()) {
                // action
                val followingList = sut.existsList(followerId, profileIds)

                // assert
                followingList shouldBe followingMatrix[idx]
            }
        }
    }
})
