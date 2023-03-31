package ndy.domain.profile.application

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.orNull
import io.kotest.property.assume
import io.kotest.property.checkAll
import ndy.domain.profile.follow.application.FollowService
import ndy.global.context.userIdContext
import ndy.global.util.requiresNewTransaction
import ndy.infra.tables.FollowTable
import ndy.infra.tables.ProfileTable
import ndy.infra.tables.UserTable
import ndy.test.extentions.DB
import ndy.test.generator.ProfileArbs.bioValueArb
import ndy.test.generator.ProfileArbs.imageFullPathArb
import ndy.test.generator.ProfileArbs.usernameValueArb
import ndy.test.generator.UserArbs.emailArb
import ndy.test.generator.UserArbs.passwordArb
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

        test("register a profile and get it's result") {
            checkAll(emailArb, passwordArb, usernameValueArb) { email, password, usernameValue ->
                // setup
                val userId = requiresNewTransaction { userRepository.save(email, password) }.id
                assumeNotDuplicated(usernameValue)

                // action
                val result = requiresNewTransaction { with(userIdContext(userId)) { sut.register(usernameValue) } }

                // assert
                assertSoftly(result) {
                    it.username shouldBe usernameValue
                    it.bio shouldBe null
                    it.image shouldBe null
                    it.following shouldBe false
                }
            }
        }

        test("register a profile and find it with userId") {
            checkAll(emailArb, passwordArb, usernameValueArb) { email, password, usernameValue ->
                // setup
                val userId = requiresNewTransaction { userRepository.save(email, password) }.id
                assumeNotDuplicated(userId.value, usernameValue)
                requiresNewTransaction { with(userIdContext(userId)) { sut.register(usernameValue) } }

                // action
                val result = requiresNewTransaction { with(userIdContext(userId)) { sut.getByUserId() } }

                // assert
                assertSoftly(result) {
                    username shouldBe usernameValue
                    bio shouldBe null
                    image shouldBe null
                    following shouldBe false
                }
            }
        }

        test("update profile") {
            checkAll(
                emailArb,
                passwordArb,
                usernameValueArb,
                bioValueArb.orNull(),
                imageFullPathArb.orNull(),
                usernameValueArb.orNull(),
            ) { email, password, username, updateBio, updateImage, updateUsername ->
                // setup - assume non duplicated username & userId
                val userId = requiresNewTransaction { userRepository.save(email, password) }.id
                assumeNotDuplicated(userId.value, username)
                updateUsername?.let { assumeNotDuplicated(it) }
                assume(username != updateUsername)

                // setup - save a profile
                requiresNewTransaction { with(userIdContext(userId)) { sut.register(username) } }

                // action - update profile
                requiresNewTransaction {
                    with(userIdContext(userId)) {
                        sut.update(
                            updateUsername,
                            updateBio,
                            updateImage
                        )
                    }
                }

                // assert - properly updated
                val profileResult = requiresNewTransaction { with(userIdContext(userId)) { sut.getByUserId() } }
                assertSoftly(profileResult) {
                    this.username shouldBeUpdatedToIf (updateUsername isNotNullOr username)
                    this.bio shouldBeUpdatedToIf (updateBio isNotNullOr null)
                    this.image shouldBeUpdatedToIf (updateImage isNotNullOr null)
                    this.following shouldBe false
                }
            }
        }

        test("exist by username") {
            checkAll(emailArb, passwordArb, usernameValueArb) { email, password, username ->
                // setup - save a profile
                val userId = requiresNewTransaction { userRepository.save(email, password) }.id
                requiresNewTransaction {
                    assumeNotDuplicated(username)
                    with(userIdContext(userId)) { sut.register(username) }
                }

                // action & assert
                requiresNewTransaction {
                    sut.checkUsernameDuplicated(username) shouldBe true
                    sut.checkUsernameDuplicated("nonExist${username}") shouldBe false
                }
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