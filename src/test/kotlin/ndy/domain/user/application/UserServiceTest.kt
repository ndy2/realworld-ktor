package ndy.domain.user.application

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.PropertyTesting
import io.kotest.property.checkAll
import ndy.domain.profile.domain.ProfileRepository
import ndy.infra.tables.UserTable
import ndy.test.extentions.DB
import ndy.test.extentions.DI
import ndy.test.extentions.JWT
import ndy.test.generator.ProfileArbs.usernameValueArb
import ndy.test.generator.UserArbs.emailValueArb
import ndy.test.generator.UserArbs.passwordValueArb
import ndy.test.spec.BaseSpec
import ndy.test.util.assumeNonDuplicatedUsername
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.test.inject

class UserServiceTest : BaseSpec(DI, DB, JWT) {

    private val sut: UserService by inject()
    private val profileRepository: ProfileRepository by inject()

    init {
        test("register with arb fields") {
            checkAll(
                usernameValueArb,
                emailValueArb,
                passwordValueArb
            ) { username, email, password ->
                assumeNonDuplicatedUsername(username, profileRepository)

                val result = sut.register(username, email, password)

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
                assumeNonDuplicatedUsername(username, profileRepository)
                sut.register(username, email, password)

                val result = sut.login(email, password)

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
}
