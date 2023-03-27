package ndy.test.generator

import ndy.domain.profile.domain.Username
import ndy.domain.user.domain.*
import ndy.test.util.ascii

@Suppress("unused") // since they are registered automatically @BaseSpec#registerCustomArbs
object ProfileArbs {

    val usernameValueArb = createArb { rs -> rs.ascii(0..10) }
    val usernameArb = createArb<Username>(usernameValueArb)
}