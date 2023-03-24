package ndy.domain.user.domain

data class User(
    val id: ULong,
    val username: Username,
    val email: Email,
    val password: Password,
)