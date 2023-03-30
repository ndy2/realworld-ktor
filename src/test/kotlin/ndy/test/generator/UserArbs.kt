package ndy.test.generator

import io.kotest.property.Arb
import io.kotest.property.arbitrary.*
import ndy.domain.user.application.BcryptPasswordService
import ndy.domain.user.domain.*

@Suppress("unused") // since they are registered automatically @BaseSpec#registerCustomArbs
object UserArbs {
    val emailValueArb = Arb.email(
        Arb.string(3..10, Codepoint.alphanumeric()),
        Arb.domain(
            tlds = listOf("com", "org", "edu", "ac.kr", "net"),
            labelArb = Arb.string(2..5, Codepoint.alphanumeric())
        )
    )
    val emailArb = emailValueArb.map { Email(it) }

    val passwordValueArb = Arb.string(8..32, Codepoint.ascii())
    val passwordEncoderArb = arbitrary { BcryptPasswordService }
    val passwordVerifierArb = arbitrary { BcryptPasswordService }
    val passwordArb = Arb.bind(passwordValueArb, passwordVerifierArb) { value, verifier -> Password(value, verifier) }
}