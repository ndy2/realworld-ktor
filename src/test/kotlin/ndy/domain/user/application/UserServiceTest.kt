package ndy.domain.user.application

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.checkAll
import ndy.domain.profile.application.ProfileService
import ndy.domain.profile.follow.application.FollowService
import ndy.infra.tables.FollowTable
import ndy.infra.tables.ProfileTable
import ndy.infra.tables.UserTable
import ndy.test.extentions.Db
import ndy.test.extentions.Jwt
import ndy.test.generator.ProfileArbs.usernameValueArb
import ndy.test.generator.UserArbs.emailValueArb
import ndy.test.generator.UserArbs.passwordValueArb
import ndy.test.spec.BaseSpec
import ndy.test.util.assumeNotDuplicated
import ndy.test.util.loggingContext

class UserServiceTest : BaseSpec(Db, Jwt, body = {

    val sut = with(loggingContext()) {
        UserService(
                repository = UserTable,
                profileService = ProfileService(
                        repository = ProfileTable,
                        followService = FollowService(FollowTable)
                ),
                passwordEncoder = BcryptPasswordService,
                passwordVerifier = BcryptPasswordService
        )
    }
    with(ProfileTable) {

        transactionalTest("register with arb fields") {
            checkAll(usernameValueArb, emailValueArb, passwordValueArb) { username, email, password ->
                // setup
                assumeNotDuplicated(username)

                // action
                val result = sut.register(username, email, password)

                // assert
                assertSoftly(result) {
                    it.username shouldBe username
                    it.email shouldBe email
                }
            }
        }

        transactionalTest("register a user and login with it") {
            checkAll(usernameValueArb, emailValueArb, passwordValueArb) { username, email, password ->
                // setup
                assumeNotDuplicated(username)
                sut.register(username, email, password)

                // action
                val result = sut.login(email, password)

                // assert
                assertSoftly(result) {
                    it.token shouldNotBe null
                    it.username shouldBe username
                    it.email shouldBe email
                    it.bio shouldBe null
                    it.image shouldBe null
                }
            }
        }
    }
})
