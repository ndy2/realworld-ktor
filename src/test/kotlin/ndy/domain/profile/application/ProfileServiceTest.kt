package ndy.domain.profile.application

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.orNull
import io.kotest.property.assume
import io.kotest.property.checkAll
import ndy.domain.profile.follow.application.FollowService
import ndy.infra.tables.FollowTable
import ndy.infra.tables.ProfileTable
import ndy.infra.tables.UserTable
import ndy.test.extentions.DB
import ndy.test.generator.ProfileArbs.bioValueArb
import ndy.test.generator.ProfileArbs.imageFullPathArb
import ndy.test.generator.ProfileArbs.usernameValueArb
import ndy.test.generator.UserArbs.userArb
import ndy.test.spec.BaseSpec
import ndy.test.util.assumeNotDuplicated
import ndy.test.util.isNotNullOr
import ndy.test.util.shouldBeUpdatedToIf

class ProfileServiceTest : BaseSpec(DB, body = {

    val sut = ProfileService(
        repository = ProfileTable,
        followService = FollowService(
            repository = FollowTable
        )
    )
    val userRepository = UserTable
    with(ProfileTable) {

        transactionalTest("register a profile and get it's result") {
            checkAll(userArb, usernameValueArb) { user, usernameValue ->
                // setup
                val userId = userRepository.save(user).id
                assumeNotDuplicated(usernameValue)

                // action
                val result = sut.register(userId, usernameValue)

                // assert
                assertSoftly(result) {
                    it.username shouldBe usernameValue
                    it.bio shouldBe null
                    it.image shouldBe null
                    it.following shouldBe false
                }
            }
        }

        transactionalTest("register a profile and find it with userId") {
            checkAll(userArb, usernameValueArb) { user, usernameValue ->
                // setup
                val userId = userRepository.save(user).id
                assumeNotDuplicated(userId.value, usernameValue)
                sut.register(userId, usernameValue)

                // action
                val result = sut.getByUserId(userId)

                // assert
                assertSoftly(result) {
                    username shouldBe usernameValue
                    bio shouldBe null
                    image shouldBe null
                    following shouldBe false
                }
            }
        }

        transactionalTest("update profile") {
            checkAll(
                /* registered user/profile arbs */
                userArb,
                usernameValueArb,

                /* update request arbs */
                bioValueArb.orNull(),
                imageFullPathArb.orNull(),
                usernameValueArb.orNull()
            ) { user, username,
                updateBio, updateImage, updateUsername ->
                // setup
                val userId = userRepository.save(user).id
                assumeNotDuplicated(userId.value, username)
                updateUsername?.let { assumeNotDuplicated(it) }
                assume(username != updateUsername)

                // setup - save a profile
                sut.register(userId, username)

                // action - update profile
                sut.update(userId, updateUsername, updateBio, updateImage)

                // assert - properly updated
                val profileResult = sut.getByUserId(userId)
                assertSoftly(profileResult) {
                    this.username shouldBeUpdatedToIf (updateUsername isNotNullOr username)
                    this.bio shouldBeUpdatedToIf (updateBio isNotNullOr null)
                    this.image shouldBeUpdatedToIf (updateImage isNotNullOr null)
                    this.following shouldBe false
                }
            }
        }

        transactionalTest("exist by username") {
            checkAll(
                userArb,
                usernameValueArb,
                usernameValueArb
            ) { user, username, notSavedUsername ->
                // setup - save a profile
                val userId = userRepository.save(user).id
                assumeNotDuplicated(username)
                sut.register(userId, username)

                // action & assert
                sut.checkUsernameDuplicated(username) shouldBe true
                sut.checkUsernameDuplicated(notSavedUsername) shouldBe false
            }
        }

        xcontext("get by username") {
            context("success if username is valid") {
                context("if authenticated and") {
                    test("get its own profile") {
                    }

                    test("get other user's profile who is followed by current user") {
                    }

                    test("get other user's profile who is not followed by current user") {
                    }
                }

                test("if not authenticated") {
                }
            }
            test("fail if username is invalid") {
            }
        }
    }

    xcontext("follow") {
        context("success if username is valid") {
        }
        test("fail if username is invalid") {
        }
    }

    xcontext("unfollow") {
        context("success if username is valid") {
        }
        test("fail if username is invalid") {
        }
    }
})
