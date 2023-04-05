@file:Suppress("IMPLICIT_NOTHING_TYPE_ARGUMENT_IN_RETURN_POSITION")

package ndy.test.context

import ndy.domain.profile.domain.ProfileId
import ndy.domain.user.domain.UserId
import ndy.global.security.Principal
import ndy.global.security.AuthenticationContext
import ndy.plugins.jwtVerifier


inline fun <R> withNoToken(block: AuthenticationContext.() -> R): R {
    return with(noAuthContext()) {
        block()
    }
}

inline fun <R> withToken(token: String, block: AuthenticationContext.() -> R): R {
    return with(authenticatedUserContext(token)) {
        block()
    }
}

fun authenticatedUserContext(token: String): AuthenticationContext {
    val claims = jwtVerifier.verify(token).claims

    val userId = UserId(claims["user_id"]!!.asLong().toULong())
    val profileId = ProfileId(claims["user_id"]!!.asLong().toULong())

    return object : AuthenticationContext {
        override val authenticated = false
        override val principal = Principal(userId, profileId)
    }
}

fun noAuthContext(): AuthenticationContext {
    return object : AuthenticationContext {
        override val authenticated = false
        override val principal: Nothing by lazy { error("no auth") }
    }
}
