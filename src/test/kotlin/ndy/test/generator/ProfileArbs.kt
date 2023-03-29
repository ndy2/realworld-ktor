package ndy.test.generator

import ndy.domain.profile.domain.Image
import ndy.domain.profile.domain.Username
import ndy.domain.user.domain.*
import ndy.test.util.alphaNumericString
import ndy.test.util.ascii

@Suppress("unused") // since they are registered automatically @BaseSpec#registerCustomArbs
object ProfileArbs {

    val usernameValueArb = createArb { rs -> rs.alphaNumericString(0..10) }
    val usernameArb = createArb<Username>(usernameValueArb)

    val imageStorePathArb = createArb { "path/to/store" }
    val imageFilNameArb = createArb { rs -> rs.alphaNumericString(5..10) }
    val imageExtensionArb = createArb { listOf("png", "jpg", "jpeg").random() }
    val imageArb = createArb<Image>(imageStorePathArb, imageExtensionArb, imageFilNameArb)
}