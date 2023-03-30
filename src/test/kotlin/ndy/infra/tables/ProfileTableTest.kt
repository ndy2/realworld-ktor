package ndy.infra.tables

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.checkAll
import ndy.domain.profile.domain.Bio
import ndy.domain.profile.domain.Image
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
            }
        }
    }

    test("update user") {
        checkAll<UserId, Username, Bio?, Image?, Username?> { userId, username, bio, image, updatedUsername ->
            newTransaction {
                sut.save(userId, username)
                val count = sut.updateByUserId(userId, updatedUsername, bio, image)

                if (listOf(bio, image, updatedUsername).any { it != null }) count shouldBe 1
                else count shouldBe 0
            }

            newTransaction {
                val foundUser = sut.findByUserId(userId)
                assertSoftly(foundUser!!) {
                    if (updatedUsername == null) it.username shouldBe username
                    else it.username shouldBe updatedUsername
                    it.bio shouldBe bio
                    it.image shouldBe image
                }
            }
        }

    }
})