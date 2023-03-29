package ndy.infra.tables

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.checkAll
import ndy.domain.profile.domain.Username
import ndy.domain.user.domain.UserId
import ndy.test.extentions.DB
import ndy.test.spec.BaseSpec
import ndy.util.newTransaction

class ProfileTableTest : BaseSpec(DB, body = {

    val sut = ProfileTable

    test("returns saved profile and find it by id") {
        checkAll<ULong, Username> { userId, username ->
            newTransaction {
                val savedUser = sut.save(UserId(userId), username)
                assertSoftly(savedUser) {

                    this.id shouldNotBe null
                    this.username shouldBe username
                    this.bio shouldBe null
                    this.image shouldBe null
                }

                val foundUsername = sut.findUsernameByUserId(UserId(userId))
                foundUsername shouldBe username.value
            }
        }
    }
})