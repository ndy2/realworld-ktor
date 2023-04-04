@file:Suppress("IMPLICIT_NOTHING_TYPE_ARGUMENT_IN_RETURN_POSITION")

package ndy.test.context

import ndy.domain.profile.domain.ProfileId
import ndy.domain.user.domain.UserId
import ndy.global.context.AuthenticatedUserContext
import ndy.plugins.jwtVerifier


inline fun <R> withNoToken(block: AuthenticatedUserContext.() -> R): R {
    return with(noAuthContext()) {
        block()
    }
}

inline fun <R> withToken(token: String, block: AuthenticatedUserContext.() -> R): R {
    return with(authenticatedUserContext(token)) {
        block()
    }
}

fun authenticatedUserContext(token: String): AuthenticatedUserContext {
    val claims = jwtVerifier.verify(token).claims

    val userId = claims["user_id"]!!.asLong().toULong()
    val profileId = claims["user_id"]!!.asLong().toULong()

    return object : AuthenticatedUserContext {
        override val authenticated = true
        override val userId = UserId(userId)
        override val profileId = ProfileId(profileId)
    }
}

fun noAuthContext(): AuthenticatedUserContext {
    return object : AuthenticatedUserContext {
        override val authenticated = false
        override val userId: Nothing by lazy { error("no auth") }
        override val profileId: Nothing by lazy { error("no auth") }
    }
}
