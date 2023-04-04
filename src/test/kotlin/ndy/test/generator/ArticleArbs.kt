package ndy.test.generator

import io.kotest.property.Arb
import io.kotest.property.arbitrary.Codepoint
import io.kotest.property.arbitrary.ascii
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.string
import ndy.test.generator.TagArbs.tagValueArb

object ArticleArbs {

    val titleValueArb = Arb.string(1..128, Codepoint.ascii())
    val descriptionValueArb = Arb.string(1..256, Codepoint.ascii())
    val bodyValueArb = Arb.string(1..512, Codepoint.ascii())
    val tagValueListArb = Arb.list(tagValueArb, 0..3)
}
