package ndy.domain.user.domain

import io.kotest.assertions.konform.shouldBeValid
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.property.checkAll
import ndy.global.exception.RealworldRuntimeException
import ndy.test.generator.UserArbs.emailValueArb
import ndy.test.spec.BaseSpec

class EmailTest : BaseSpec(body = {

    test("email arb works properly") {
        checkAll<Email> { email -> validateEmail shouldBeValid email }
    }

    test("email validation work properly with constructor") {
        checkAll(emailValueArb) { shouldNotThrow<RealworldRuntimeException> { Email(it) } }
        checkAll<String> { shouldThrow<RealworldRuntimeException> { Email(it) } }
    }
})