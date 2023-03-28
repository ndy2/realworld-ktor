package ndy.domain.user.domain

import io.ktor.server.auth.*

data class User(
    val id: UserId,
    val email: Email,
    val password: Password,
)

@JvmInline
value class UserId(
    val value: ULong
) : Principal