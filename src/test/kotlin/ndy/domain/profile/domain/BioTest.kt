package ndy.domain.profile.domain

import io.kotest.matchers.shouldBe
import io.kotest.property.checkAll
import ndy.test.generator.ProfileArbs.bioValueArb
import ndy.test.spec.BaseSpec

class BioTest : BaseSpec(body = {

    test("create bio") {
        checkAll(bioValueArb) { value ->
            val bio = Bio(value)

            bio.value shouldBe value
        }
    }
})