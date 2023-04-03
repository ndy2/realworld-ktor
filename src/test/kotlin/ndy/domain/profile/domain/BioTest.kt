package ndy.domain.profile.domain

import io.kotest.assertions.konform.shouldBeValid
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.property.Arb
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import ndy.global.exception.RealworldRuntimeException
import ndy.test.generator.ProfileArbs.bioArb
import ndy.test.generator.ProfileArbs.bioValueArb
import ndy.test.spec.BaseSpec

class BioTest : BaseSpec(body = {

    test("bioArb generates valid bio") {
        checkAll(bioArb) { validateBio shouldBeValid it }
    }

    test("bio validation work properly") {
        // success
        checkAll(bioValueArb) { shouldNotThrow<RealworldRuntimeException> { Bio(it) } }

        // fail if too long
        checkAll(Arb.string(Bio.MAX_LENGTH + 1, 1000)) { shouldThrow<RealworldRuntimeException> { Bio(it) } }
    }
})
