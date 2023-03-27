package ndy.test.generator

import ndy.domain.user.application.BcryptPasswordService
import ndy.domain.user.domain.*
import ndy.test.util.alphaNumericString
import ndy.test.util.ascii

@Suppress("unused") // since they are registered automatically @BaseSpec#registerCustomArbs
object UserArbs {
    val usernameValueArb = createArb { rs -> rs.ascii(0..10) }
    val usernameArb = createArb<Username>(usernameValueArb)

    val emailValueArb = createArb { rs ->
        val username = rs.alphaNumericString(0..10)
        val domainName = rs.alphaNumericString(0..5)
        val domainExtension = listOf("com", "org", "edu", "ac.kr", "net").random()
        "$username@$domainName.$domainExtension"
    }
    val emailArb = createArb<Email>(emailValueArb)

    val passwordValueArb = createArb { rs -> rs.ascii(8..15) }
    val passwordEncoderArb = createArb<PasswordEncoder> { _ -> BcryptPasswordService }
    val passwordVerifierArb = createArb<PasswordVerifier> { _ -> BcryptPasswordService }
    val passwordArb = createArb<Password>(passwordValueArb, passwordEncoderArb)
}