package ndy.infra.tables

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.assume
import io.kotest.property.checkAll
import ndy.domain.profile.domain.Bio
import ndy.domain.profile.domain.Image
import ndy.domain.profile.domain.Username
import ndy.domain.user.domain.UserId
import ndy.test.extentions.DB
import ndy.test.spec.BaseSpec
import ndy.test.util.assumeNotDuplicated
import ndy.test.util.isNotNullOr
import ndy.test.util.shouldBeUpdatedToIf
import ndy.util.newTransaction

class ProfileTableTest : BaseSpec(DB, body = {

    val sut = ProfileTable

    with(sut) {
        test("returns saved profile and find it") {
            checkAll<UserId, Username> { userId, username ->
                // setup - assume not duplicated username & id
                assumeNotDuplicated(userId.value, username.value)

                // save it
                val savedProfile = newTransaction { sut.save(userId, username) }
                // assert it
                assertSoftly(savedProfile) {
                    this.id shouldNotBe null
                    this.username shouldBe username
                    this.bio shouldBe null
                    this.image shouldBe null
                }

                // find it
                val foundProfile = newTransaction { sut.findById(savedProfile.id) }
                // assert it
                assertSoftly(foundProfile!!) {
                    this.id shouldBe savedProfile.id
                    this.username shouldBe savedProfile.username
                    this.bio shouldBe savedProfile.bio
                    this.image shouldBe savedProfile.image
                }
            }
        }

        test("update profile") {
            checkAll<UserId, Username, Bio?, Image?, Username?> { userId, username, updateBio, updateImage, updateUsername ->
                // assume non duplicated username & userId
                assumeNotDuplicated(userId.value, username.value)
                updateUsername?.let { assumeNotDuplicated(it.value) }
                assume(username != updateUsername)

                // save a profile
                newTransaction {
                    sut.save(userId, username)
                    val count = sut.updateByUserId(userId, updateUsername, updateBio, updateImage)
                    if (listOf(updateBio, updateImage, updateUsername).any { it != null }) count shouldBe 1
                    else count shouldBe 0
                }

                // update it
                val foundProfile = newTransaction { sut.findByUserId(userId) }

                // assert
                assertSoftly(foundProfile!!) {
                    it.username shouldBeUpdatedToIf (updateUsername isNotNullOr username)
                    it.bio shouldBe updateBio
                    it.image shouldBe updateImage
                }

            }
        }

        test("exist by username") {
            checkAll<UserId, Username> { userId, username ->
                // save a profile
                newTransaction {
                    assumeNotDuplicated(username.value)
                    sut.save(userId, username)
                }

                // check exists
                newTransaction {
                    sut.existByUsername(username) shouldBe true
                    sut.existByUsername(Username("nonExist${username.value}")) shouldBe false
                }
            }
        }
    }
})