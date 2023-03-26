package ndy.test.generator

import io.kotest.property.Arb
import io.kotest.property.arbitrary.ArbitraryBuilder
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.string
import ndy.domain.user.domain.Email
import ndy.domain.user.domain.Password
import ndy.domain.user.domain.Username
import ndy.test.util.alphaNumericString
import ndy.test.util.ascii

object UserArbs {

    object UsernameValueArb : ArbNullEdgecase<String>({
        Arb
            .string(0..10)
            .sample(it)
    })

    object UsernameArb : ArbNullEdgecase<Username>({ UsernameValueArb.map(::Username).sample(it) })

    object EmailValueArb : ArbNullEdgecase<String>({ rs ->
        ArbitraryBuilder.create {
            val username = rs.alphaNumericString(0..10)
            val domainName = rs.alphaNumericString(0..5)
            val domainExtension = listOf("com", "org", "edu", "ac.kr", "net").random()
            "$username@$domainName.$domainExtension"
        }.build().sample(rs)
    })

    object EmailArb : ArbNullEdgecase<Email>({ EmailValueArb.map(::Email).sample(it) })

    object PasswordValueArb : ArbNullEdgecase<String>({ rs ->
        ArbitraryBuilder.create {
            rs.ascii(8..15)
        }.build().sample(rs)
    })

    object PasswordArb : ArbNullEdgecase<Password>({ PasswordValueArb.map(::Password).sample(it) })
}


