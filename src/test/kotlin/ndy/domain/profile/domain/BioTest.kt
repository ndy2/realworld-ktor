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
        checkAll(bioValueArb) { shouldNotThrow<RealworldRuntimeException> { Bio(it) } }

        val tooLongBioArb = Arb.string(minSize = Bio.MAX_LENGTH + 1, maxSize = 1000)
        checkAll(tooLongBioArb) { shouldThrow<RealworldRuntimeException> { Bio(it) } }
    }
})