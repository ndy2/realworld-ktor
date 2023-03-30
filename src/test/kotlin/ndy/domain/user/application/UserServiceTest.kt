package ndy.domain.user.application

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.PropertyTesting
import io.kotest.property.checkAll
import ndy.context.DefaultLoggingContext
import ndy.domain.profile.application.ProfileService
import ndy.infra.tables.ProfileTable
import ndy.infra.tables.UserTable
import ndy.test.extentions.DB
import ndy.test.extentions.JWT
import ndy.test.generator.ProfileArbs.usernameValueArb
import ndy.test.generator.UserArbs.emailValueArb
import ndy.test.generator.UserArbs.passwordValueArb
import ndy.test.spec.BaseSpec
import ndy.test.util.assumeNotDuplicated
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class UserServiceTest : BaseSpec(DB, JWT, body = {

    val sut = with(DefaultLoggingContext) {
        UserService(
            repository = UserTable,
            profileService = ProfileService(ProfileTable),
            passwordEncoder = BcryptPasswordService,
            passwordVerifier = BcryptPasswordService
        )
    }

    with(ProfileTable) {
        test("register with arb fields") {
            checkAll(
                usernameValueArb,
                emailValueArb,
                passwordValueArb
            ) { username, email, password ->
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

            transaction {
                val count = UserTable.Users.selectAll().count()
                count shouldBe PropertyTesting.defaultIterationCount
            }
        }

        test("register a user and login with it") {
            checkAll(
                usernameValueArb,
                emailValueArb,
                passwordValueArb
            ) { username, email, password ->
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
