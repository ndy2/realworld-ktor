package ndy.domain.user.domain

import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import ndy.exception.RealworldRuntimeException
import ndy.test.generator.UserArbs.passwordEncoderArb
import ndy.test.generator.UserArbs.passwordValueArb
import ndy.test.generator.UserArbs.passwordVerifierArb
import ndy.test.spec.BaseSpec

class PasswordTest : BaseSpec(body = {

    test("password 생성 후 검증 성공") {

        checkAll<String, PasswordEncoder, PasswordVerifier> { passwordValue, encoder, verifier ->
            // construct password
            val password = Password(passwordValue, encoder)

            // check it with raw password
            shouldNotThrow<RealworldRuntimeException> { password.checkPassword(passwordValue, verifier) }

            // create password with encoded value
            val passwordWithEncoded = Password.withEncoded(password.encodedPassword)

            // both should be equal
            passwordWithEncoded shouldBe password
        }
    }

    test("checkPassword 성공/실패") {
        checkAll(
            passwordValueArb,
            passwordEncoderArb,
            passwordVerifierArb,
            Arb.string()
        ) { passwordValue, encoder, verifier, arbString ->
            // construct password
            val password = Password(passwordValue, encoder)

            // check success with password
            shouldNotThrow<RealworldRuntimeException> { password.checkPassword(passwordValue, verifier) }

            // check failure with arb string
            shouldThrow<RealworldRuntimeException> { password.checkPassword(arbString, verifier) }
                .apply { message shouldBe "login failure" }
        }
    }

    test("cannot access encodedPassword with null args constructed Password"){
        val nullPassword = Password(rawPassword = null, passwordEncoder = null)

        shouldThrow<RealworldRuntimeException> { nullPassword.encodedPassword /* invoke PasswordDelegate.getValue */ }
            .apply { message shouldBe "illegal approach to get encodedPassword" }
    }
})