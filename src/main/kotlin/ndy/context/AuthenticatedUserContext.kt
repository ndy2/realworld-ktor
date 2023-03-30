package ndy.context

import io.ktor.server.application.*
import ndy.domain.user.domain.UserId
import ndy.util.userId

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
}

fun authenticatedUserContext(call: ApplicationCall) =
    object : AuthenticatedUserContext {
        override val userId by lazy { call.userId()!! }
        override val userIdNullable = call.userId()
    }
