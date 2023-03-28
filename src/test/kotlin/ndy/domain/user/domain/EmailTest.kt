package ndy.domain.user.domain

import io.kotest.assertions.konform.shouldBeValid
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.property.checkAll
import ndy.exception.RealworldRuntimeException
import ndy.test.generator.UserArbs.emailValueArb
import ndy.test.spec.BaseSpec

class EmailTest : BaseSpec(body = {

    test("validateEmail works properly") {
        checkAll<Email> { email ->
            validateEmail shouldBeValid email
        }
    }

    test("email validation work properly with constructor") {
        checkAll(emailValueArb) { emailValue ->
            shouldNotThrow<RealworldRuntimeException> { Email(emailValue) }
        }
        checkAll<String> { arbString ->
            shouldThrow<RealworldRuntimeException> { Email(arbString) }
        }
    }
})