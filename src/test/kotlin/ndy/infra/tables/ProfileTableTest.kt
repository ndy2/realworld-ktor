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

                // action - save it
                val savedProfile = newTransaction { sut.save(userId, username) }

                // assert
                assertSoftly(savedProfile) {
                    this.id shouldNotBe null
                    this.username shouldBe username
                    this.bio shouldBe null
                    this.image shouldBe null
                }

                // action - find it
                val foundProfile = newTransaction { sut.findById(savedProfile.id) }

                // assert
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
                // setup - assume non duplicated username & userId
                assumeNotDuplicated(userId.value, username.value)
                updateUsername?.let { assumeNotDuplicated(it.value) }
                assume(username != updateUsername)

                // action - save a profile
                val count = newTransaction {
                    sut.save(userId, username)
                    sut.updateByUserId(userId, updateUsername, updateBio, updateImage)
                }

                // assert - update count
                if (listOf(updateBio, updateImage, updateUsername).any { it != null }) count shouldBe 1
                else count shouldBe 0

                // assert - properly updated
                val foundProfile = newTransaction { sut.findByUserId(userId) }
                assertSoftly(foundProfile!!) {
                    it.username shouldBeUpdatedToIf (updateUsername isNotNullOr username)
                    it.bio shouldBe updateBio
                    it.image shouldBe updateImage
                }
            }
        }

        test("exist by username") {
            checkAll<UserId, Username> { userId, username ->
                // setup - save a profile
                newTransaction {
                    assumeNotDuplicated(username.value)
                    sut.save(userId, username)
                }

                // action & assert
                newTransaction {
                    sut.existByUsername(username) shouldBe true
                    sut.existByUsername(Username("nonExist${username.value}")) shouldBe false
                }
            }
        }

        test("find by username") {
            checkAll<UserId, Username> { userId, username ->
                // setup - save a profile
                newTransaction {
                    assumeNotDuplicated(username.value)
                    sut.save(userId, username)
                }

                // action
                val foundProfile = newTransaction { sut.findByUsername(username) }

                // assert
                foundProfile shouldNotBe null
                assertSoftly(foundProfile!!) {
                    this.id shouldNotBe null
                    this.userId shouldBe userId
                    this.username shouldBe username
                    this.bio shouldBe null
                    this.image shouldBe null
                }
            }
        }
    }
})