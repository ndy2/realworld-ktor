package ndy.test.generator

import io.kotest.property.Arb
import io.kotest.property.arbitrary.Codepoint
import io.kotest.property.arbitrary.alphanumeric
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.ascii
import io.kotest.property.arbitrary.choice
import io.kotest.property.arbitrary.flatMap
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.of
import io.kotest.property.arbitrary.string
import io.kotest.property.arbitrary.uLong
import ndy.domain.profile.domain.Bio
import ndy.domain.profile.domain.Image
import ndy.domain.profile.domain.Username
import ndy.domain.user.domain.UserId

@Suppress("unused") // non-primitive arbs are registered automatically @BaseSpec#registerCustomArbs
object ProfileArbs {

    val userIdArb = Arb.uLong(1u, ULong.MAX_VALUE / 2u).map(::UserId)

    /* Username Arbs */
    val usernameValueArb = Arb.string(
        Username.MIN_LENGTH..Username.MAX_LENGTH,
        Arb.of((('a'..'z') + ('A'..'Z') + ('0'..'9') + ('_') + ('-')).map { Codepoint(it.code) })
        // Codepoint of alphanumeric with `_` and `-`
    )
    val usernameArb = usernameValueArb.map { Username(it) }

    /* Image Arbs */
    val imageStorePathArb = arbitrary { "path/to/store" }
    val imageFileNameArb = Arb.string(Image.FILE_NAME_MIN_LENGTH..Image.FILE_NAME_MAX_LENGTH, Codepoint.alphanumeric())
    val imageExtensionArb = Arb.choice(Image.ALLOWED_EXTENSIONS.map { ext -> arbitrary { ext } })
    val imageFullPathArb = imageStorePathArb.flatMap { storePath ->
        imageFileNameArb.flatMap { fileName ->
            imageExtensionArb.map { extension ->
                "$storePath/$fileName.$extension"
            }
        }
    }
    val imageArb = imageFullPathArb.map { Image.ofFullPath(it) }

    /* Bio Arbs */
    val bioValueArb = Arb.string(0..512, Codepoint.ascii())
    val bioArb = bioValueArb.map { Bio(it) }
}
