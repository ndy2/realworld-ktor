package ndy.global.context

import io.ktor.server.application.*
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

    // use it in context of authenticate(optional = false)
    val userId: UserId

    // use it in context of authenticate(optional = false)
    val profileId: ProfileId
}

fun authenticatedUserContext(call: ApplicationCall, optional: Boolean) =
    object : AuthenticatedUserContext {
        override val authenticated = optional

        // refer userId with no principal is not allowed
        override val userId by lazy { (call.authentication.principal() as? Principal)?.userId!! }

        // refer profileId with no principal is not allowed
        override val profileId by lazy { (call.authentication.principal() as? Principal)?.profileId!! }
    }

/**
 * context of userId
 * *
 * used in case of calling other domain's service @ service level
 * e.g. userService.register -> profileService.register
 * *
 * this might be bad decision for the complexity or dependency point of view.
 * but I introduced it for just fun! - apply context receiver
 */
interface UserIdContext {
    val userId: ULong
}

context (AuthenticatedUserContext)
fun userIdContext(): UserIdContext {
    return object : UserIdContext {
        override val userId: ULong = this@AuthenticatedUserContext.userId.value
    }
}

fun userIdContext(userId: UserId): UserIdContext {
    return object : UserIdContext {
        override val userId: ULong = userId.value
    }
}
