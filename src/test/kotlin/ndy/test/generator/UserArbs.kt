package ndy.test.generator

import ndy.domain.user.domain.Email
import ndy.domain.user.domain.Password
import ndy.domain.user.domain.Username
import ndy.test.util.alphaNumericString
import ndy.test.util.ascii

@Suppress("unused")
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
    val passwordArb = createArb<Password>(passwordValueArb)
}