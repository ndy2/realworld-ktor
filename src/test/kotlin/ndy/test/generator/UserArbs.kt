package ndy.test.generator

import io.kotest.property.Arb
import io.kotest.property.arbitrary.*
import ndy.domain.user.application.BcryptPasswordService
import ndy.domain.user.domain.*

@Suppress("unused") // non-primitive arbs are registered automatically @BaseSpec#registerCustomArbs
object UserArbs {
    /* Email Arbs */
    val emailValueArb = Arb.email(
        Arb.string(3..10, Codepoint.alphanumeric()),
        Arb.domain(
            tlds = listOf("com", "org", "edu", "ac.kr", "net"),
            labelArb = Arb.string(2..5, Codepoint.alphanumeric())
        )
    )
    val emailArb = emailValueArb.map { Email(it) }

    /* Password Arbs */
    val passwordValueArb = Arb.string(8..32, Codepoint.ascii())
    val passwordArb = passwordValueArb.map { Password(it, BcryptPasswordService) }

    /* User Arb */
    val userArb = Arb.bind(emailArb, passwordArb)
    { email, password ->
        User(
            email = email,
            password = password
        )
    }
}
