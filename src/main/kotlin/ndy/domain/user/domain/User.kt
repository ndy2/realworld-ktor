package ndy.domain.user.domain

import io.ktor.server.auth.*
import ndy.domain.profile.domain.Profile

data class User(
    val id: UserId,
    val email: Email,
    val password: Password,
    val profile: Profile? = null,
)

@JvmInline
value class UserId(val value: ULong) : Principal