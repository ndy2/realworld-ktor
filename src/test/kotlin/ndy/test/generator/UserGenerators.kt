package ndy.test.generator

import io.kotest.property.Arb
import io.kotest.property.RandomSource
import io.kotest.property.arbitrary.ArbitraryBuilder
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.string
import ndy.domain.user.domain.Email
import ndy.domain.user.domain.Password
import ndy.domain.user.domain.Username
import ndy.test.util.alphaNumericString
import ndy.test.util.ascii

object UserArbs {

    object UsernameGenerator : Arb<Username>() {
        override fun edgecase(rs: RandomSource) = null
        override fun sample(rs: RandomSource) = Arb
            .string(0..10)
            .map { name -> Username(name) }
            .sample(rs)
    }

    object EmailGenerator : Arb<Email>() {
        override fun edgecase(rs: RandomSource) = null
        override fun sample(rs: RandomSource) = ArbitraryBuilder.create {
            val username = rs.alphaNumericString(0..10)
            val domainName = rs.alphaNumericString(0..5)
            val domainExtension = listOf("com", "org", "edu", "ac.kr", "net").random()
            Email("$username@$domainName.$domainExtension")
        }.build().sample(rs)
    }

    object PasswordGenerator : Arb<Password>() {
        override fun edgecase(rs: RandomSource) = null
        override fun sample(rs: RandomSource) = ArbitraryBuilder.create {
            Password(rs.ascii(8..15))

        }.build().sample(rs)
    }
}


