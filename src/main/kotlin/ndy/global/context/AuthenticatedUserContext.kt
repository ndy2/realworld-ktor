package ndy.global.context

import io.ktor.server.auth.*
import ndy.domain.profile.domain.ProfileId
import ndy.domain.user.domain.UserId
import ndy.global.security.Principal

/**
 * context of authenticated user
 * *
 * see RouteUtils.authenticatedXXX for details
 * reference - https://youtu.be/NxDIq-rFXUM
 */
interface AuthenticatedUserContext {

    val authenticated: Boolean

    val userId: UserId

    val profileId: ProfileId
}

fun authenticatedUserContext(authentication: AuthenticationContext) =
    object : AuthenticatedUserContext {
        override val authenticated = (authentication.principal() as? Principal) != null

        // refer userId with no principal is not allowed
        override val userId by lazy {
            (authentication.principal() as? Principal)?.userId
                ?: error("illegal userId access")
        }

        // refer profileId with no principal is not allowed
        override val profileId by lazy {
            (authentication.principal() as? Principal)?.profileId
                ?: error("illegal profileId access")
        }
    }
