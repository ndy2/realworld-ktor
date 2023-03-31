package ndy.global.security

import ndy.domain.profile.domain.ProfileId
import ndy.domain.user.domain.UserId

data class Principal(
    val userId: UserId,
    val profileId: ProfileId,
) : io.ktor.server.auth.Principal