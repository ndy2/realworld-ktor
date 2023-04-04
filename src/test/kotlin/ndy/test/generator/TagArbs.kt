package ndy.test.generator

import io.kotest.property.Arb
import io.kotest.property.arbitrary.Codepoint
import io.kotest.property.arbitrary.alphanumeric
import io.kotest.property.arbitrary.string

object TagArbs {

    val tagValueArb = Arb.string(1..32, Codepoint.alphanumeric())
}
