package ndy.domain.user.domain

import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.matchers.shouldBe
import io.kotest.property.checkAll
import ndy.exception.RealworldRuntimeException
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
})