package ndy.domain.user.application

data class UserRegisterResult(
    val username: String,
    val email: String,
)

data class UserLoginResult(
    val email: String,
    val token: String?,
    val username: String,
    val bio: String?,
    val image: String?,
)

data class UserResult(
    val email: String,
    val username: String,
    val bio: String?,
    val image: String?,
)