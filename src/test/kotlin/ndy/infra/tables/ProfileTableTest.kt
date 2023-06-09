package ndy.infra.tables

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.assume
import io.kotest.property.checkAll
import ndy.domain.profile.domain.Bio
import ndy.domain.profile.domain.Image
import ndy.domain.profile.domain.Username
import ndy.domain.user.domain.User
import ndy.test.extentions.Db
import ndy.test.spec.BaseSpec
import ndy.test.util.assumeNotDuplicated
import ndy.test.util.isNotNullOr
import ndy.test.util.shouldBeUpdatedToIf

class ProfileTableTest : BaseSpec(Db, body = {

    val sut = ProfileTable
    val userRepository = UserTable

    with(sut) {
        transactionalTest("returns saved profile and find it") {
            checkAll<User, Username> { user, username ->
                // setup - assume not duplicated username & id
                val userId = userRepository.save(user).id
                assumeNotDuplicated(userId.value, username.value)

                // action - save it
                val savedProfile = sut.save(userId, username)

                // assert
                assertSoftly(savedProfile) {
                    this.id shouldNotBe null
                    this.username shouldBe username
                    this.bio shouldBe null
                    this.image shouldBe null
                }

                // action - find it
                val foundProfile = sut.findById(savedProfile.id)

                // assert
                assertSoftly(foundProfile!!) {
                    this.id shouldBe savedProfile.id
                    this.username shouldBe savedProfile.username
                    this.bio shouldBe savedProfile.bio
                    this.image shouldBe savedProfile.image
                }
            }
        }

        transactionalTest("update profile") {
            checkAll<User, Username, Bio?, Image?, Username?> { user, username, uBio, uImage, uUsername ->
                // setup - assume non duplicated username & userId
                val userId = userRepository.save(user).id
                assumeNotDuplicated(userId.value, username.value)
                uUsername?.let { assumeNotDuplicated(it.value) }
                assume(username != uUsername)

                // action - save a profile
                sut.save(userId, username)
                val count = sut.updateByUserId(userId, uUsername, uBio, uImage)

                // assert - update count
                if (listOf(uBio, uImage, uUsername).any { it != null }) {
                    count shouldBe 1
                } else {
                    count shouldBe 0
                }

                // assert - properly updated
                val foundProfile = sut.findByUserId(userId)
                assertSoftly(foundProfile!!) {
                    it.username shouldBeUpdatedToIf (uUsername isNotNullOr username)
                    it.bio shouldBe uBio
                    it.image shouldBe uImage
                }
            }
        }

        transactionalTest("exist by username") {
            checkAll<User, Username, Username> { user, username, notSavedUsername ->
                // setup - save a profile
                val userId = userRepository.save(user).id
                assumeNotDuplicated(username.value)
                sut.save(userId, username)

                // action & assert
                sut.existByUsername(username) shouldBe true
                sut.existByUsername(notSavedUsername) shouldBe false
            }
        }

        transactionalTest("find by username") {
            checkAll<User, Username, Username> { user, username, notSavedUsername ->
                // setup - save a profile
                val userId = userRepository.save(user).id
                assumeNotDuplicated(username.value)
                sut.save(userId, username)

                // action
                val profile = sut.findProfileByUsername(username)!!
                val notSavedUsernameProfile = sut.findProfileByUsername(notSavedUsername)

                // assert
                notSavedUsernameProfile shouldBe null
                assertSoftly(profile) {
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
