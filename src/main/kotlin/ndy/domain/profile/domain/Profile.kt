package ndy.domain.profile.domain

data class Profile(
    val id: ProfileId,
    val username: Username,
    val bio: Bio?,
    val image: Image?
)

@JvmInline
value class ProfileId(
    val value: ULong
)