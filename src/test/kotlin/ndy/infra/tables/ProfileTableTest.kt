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

    test("returns saved profile and find it") {
        checkAll<ULong, Username> { userId, username ->
            newTransaction {
                val savedProfile = sut.save(UserId(userId), username)
                assertSoftly(savedProfile) {

                    this.id shouldNotBe null
                    this.username shouldBe username
                    this.bio shouldBe null
                    this.image shouldBe null
                }

                val foundProfile = sut.findById(savedProfile.id)
                assertSoftly(foundProfile!!) {
                    this.id shouldBe savedProfile.id
                    this.username shouldBe savedProfile.username
                    this.bio shouldBe savedProfile.bio
                    this.image shouldBe savedProfile.image
                }

                val foundUsername = sut.findUsernameByUserId(UserId(userId))
                foundUsername shouldBe username.value
            }
        }
    }
})