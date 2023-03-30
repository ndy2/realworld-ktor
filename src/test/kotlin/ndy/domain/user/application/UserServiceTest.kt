package ndy.domain.user.application

import de.sharpmind.ktor.EnvConfig
import io.kotest.assertions.assertSoftly
import io.kotest.core.listeners.BeforeSpecListener
import io.kotest.core.spec.Spec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.checkAll
import io.ktor.server.config.*
import ndy.context.DefaultLoggingContext
import ndy.domain.profile.application.FollowService
import ndy.domain.profile.application.ProfileService
import ndy.infra.tables.ProfileTable
import ndy.infra.tables.UserTable
import ndy.test.extentions.DB
import ndy.test.generator.ProfileArbs.usernameValueArb
import ndy.test.generator.UserArbs.emailValueArb
import ndy.test.generator.UserArbs.passwordValueArb
import ndy.test.spec.BaseSpec
import ndy.test.util.assumeNotDuplicated

class UserServiceTest : BaseSpec(DB, JWT, body = {

    val sut = with(DefaultLoggingContext) {
        UserService(
            repository = UserTable,
            profileService = ProfileService(ProfileTable, FollowService()),
            passwordEncoder = BcryptPasswordService,
            passwordVerifier = BcryptPasswordService
        )
    }
    with(ProfileTable) {

        test("register with arb fields") {
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

        test("register a user and login with it") {
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

object JWT : BeforeSpecListener {
    override suspend fun beforeSpec(spec: Spec) {
        EnvConfig.initConfig(
            MapApplicationConfig(
                "envConfig.default.jwt.domain" to "https://jwt-provider-domain/",
                "envConfig.default.jwt.issuer" to "ndy2",
                "envConfig.default.jwt.audience" to "jwt-audience",
                "envConfig.default.jwt.realm" to "ktor sample app",
                "envConfig.default.jwt.secret" to "secret",
                "envConfig.default.jwt.expires" to "60000",
            )
        )
    }
}