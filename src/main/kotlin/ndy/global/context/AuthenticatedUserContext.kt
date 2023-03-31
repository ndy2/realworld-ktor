package ndy.global.context

import io.ktor.server.application.*
import ndy.domain.profile.domain.ProfileId
import ndy.domain.user.domain.UserId
import ndy.global.util.profileId
import ndy.global.util.userId

/**
 * context of authenticated user
 * *
 * see RouteUtils.authenticatedXXX for details
 */
interface AuthenticatedUserContext {

    // use it in context of authenticate(optional = false)
    val userId: UserId

    // use it in context of authenticate(optional = true)
    val userIdNullable: UserId?

    // use it in context of authenticate(optional = false)
    val profileId: ProfileId

    // use it in context of authenticate(optional = true)
    val profileIdNullable: ProfileId?
}

fun authenticatedUserContext(call: ApplicationCall) =
    object : AuthenticatedUserContext {
        override val userId by lazy { call.userId()!! }
        override val userIdNullable = call.userId()
        override val profileId by lazy { call.profileId()!! }
        override val profileIdNullable = call.profileId()
    }

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
