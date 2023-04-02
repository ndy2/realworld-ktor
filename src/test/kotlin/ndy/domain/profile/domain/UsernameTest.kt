package ndy.domain.profile.domain

import io.kotest.assertions.konform.shouldBeValid
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.property.checkAll
import ndy.global.exception.RealworldRuntimeException
import ndy.test.generator.ProfileArbs.usernameArb
import ndy.test.generator.ProfileArbs.usernameValueArb
import ndy.test.spec.BaseSpec

class UsernameTest : BaseSpec(body = {

    test("usernameArb generates valid usernames") {
        checkAll(usernameArb) { validateUsername shouldBeValid it }
    }

    test("username validation work properly") {
        checkAll(usernameValueArb) { shouldNotThrow<RealworldRuntimeException> { Username(it) } }
        checkAll<String> { shouldThrow<RealworldRuntimeException> { Username(it) } }
    }
})
