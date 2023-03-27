package ndy.domain.user.domain

data class User(
    val id: ULong,
    val email: Email,
    val password: Password,
)