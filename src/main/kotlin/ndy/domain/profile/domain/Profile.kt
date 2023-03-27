package ndy.domain.profile.domain

data class Profile(
    val id: ULong,
    val username: Username,
    val bio: Bio?,
    val image: Image?
)
