package ndy.domain.user.domain

import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.string
import io.kotest.property.azstring
import io.kotest.property.checkAll
import ndy.domain.user.application.BcryptPasswordService
import ndy.global.exception.RealworldRuntimeException
import ndy.test.generator.UserArbs.passwordValueArb
import ndy.test.spec.BaseSpec

import kotlin.random.Random

class PasswordTest : BaseSpec(body = {

    val encoder = BcryptPasswordService
    val verifier = BcryptPasswordService

    test("password with Encoded") {
        checkAll(passwordValueArb) { passwordValue ->
            // setup - construct password
            val password = Password(passwordValue, encoder)

            // assert - check it with raw password
            shouldNotThrow<RealworldRuntimeException> { password.checkPassword(passwordValue, verifier) }

            // setup - create password with encoded value
            val passwordWithEncoded = Password.withEncoded(password.encodedPassword)

            // assert - both should be equal
            passwordWithEncoded shouldBe password
        }
    }

    test("checkPassword") {
        checkAll(passwordValueArb, Arb.string()) { passwordValue, arbString ->
            // setup - construct password
            val password = Password(passwordValue, encoder)

            // assert - check success with password &  check failure with arb string
            shouldNotThrow<RealworldRuntimeException> { password.checkPassword(passwordValue, verifier) }
            shouldThrow<RealworldRuntimeException> { password.checkPassword(arbString, verifier) }
                .apply { message shouldBe "login failure" }
        }
    }

    test("cannot create password with length greater than ${Password.MAX_LENGTH}") {
        // setup
        val tooLongPasswordValue = Random.azstring(Password.MAX_LENGTH + 1)

        // assert
        shouldThrow<RealworldRuntimeException> { Password(tooLongPasswordValue, null) }
    }
})
