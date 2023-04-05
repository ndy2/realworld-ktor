@file:Suppress("IMPLICIT_NOTHING_TYPE_ARGUMENT_IN_RETURN_POSITION")

package ndy.test.context

import ndy.domain.profile.domain.ProfileId
import ndy.domain.user.domain.UserId
import ndy.global.security.Principal
import ndy.ktor.context.auth.AuthenticationContext
import ndy.plugins.jwtVerifier


inline fun <R> withNoToken(block: AuthenticationContext<Principal>.() -> R): R {
    return with(noAuthContext()) {
        block()
    }
}

inline fun <R> withToken(token: String, block: AuthenticationContext<Principal>.() -> R): R {
    return with(authenticatedUserContext(token)) {
        block()
    }
}

fun authenticatedUserContext(token: String): AuthenticationContext<Principal> {
    val claims = jwtVerifier.verify(token).claims

    val userId = UserId(claims["user_id"]!!.asLong().toULong())
    val profileId = ProfileId(claims["user_id"]!!.asLong().toULong())

    return object : AuthenticationContext<Principal> {
        override val authenticated = false
        override val principal = Principal(userId, profileId)
    }
}

fun noAuthContext(): AuthenticationContext<Principal> {
    return object : AuthenticationContext<Principal> {
        override val authenticated = false
        override val principal: Nothing by lazy { error("no auth") }
    }
}
