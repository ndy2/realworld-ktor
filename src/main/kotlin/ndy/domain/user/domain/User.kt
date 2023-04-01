package ndy.domain.user.domain

import ndy.domain.profile.domain.Profile

// TODO - remove profile
data class User(
    val id: UserId,
    val email: Email,
    val password: Password,
    val profile: Profile? = null,
)

@JvmInline
value class UserId(val value: ULong)