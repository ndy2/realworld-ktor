package ndy.test.generator

import io.kotest.property.Arb
import io.kotest.property.arbitrary.*
import ndy.domain.profile.domain.Bio
import ndy.domain.profile.domain.Image
import ndy.domain.profile.domain.Username
import ndy.domain.user.domain.*

@Suppress("unused") // since they are registered automatically @BaseSpec#registerCustomArbs
object ProfileArbs {
    val userIdArb = Arb.uLong().map(::UserId)

    val usernameValueArb = Arb.string(4..64, Codepoint.alphanumeric())
    val usernameArb = usernameValueArb.map { Username(it) }

    val imageStorePathArb = arbitrary { "path/to/store" }
    val imageFilNameArb = Arb.string(5..10, Codepoint.alphanumeric())
    val imageExtensionArb = Arb.choice(arbitrary { "jpeg" }, arbitrary { "jpg" }, arbitrary { "png" })
    val imageArb = Arb.bind(imageStorePathArb, imageFilNameArb, imageExtensionArb) { store, filename, extension ->
        Image(store, filename, extension)
    }

    val bioValueArb = Arb.string(0..512, Codepoint.ascii())
    val bioArb = bioValueArb.map { Bio(it) }
}