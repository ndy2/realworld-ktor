package ndy.domain.user.application

data class UserResult(
    val email: String,
    val token: String?,
    val username: String,
    val bio: String?,
    val image: String?,
) {
    // TODO add companion object -> factory method - from
}