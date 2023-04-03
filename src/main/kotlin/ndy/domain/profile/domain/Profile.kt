package ndy.domain.profile.domain

import ndy.domain.user.domain.UserId

data class Profile(
        val id: ProfileId,
        val userId: UserId,
        val username: Username,
        val bio: Bio?,
        val image: Image?
)

@JvmInline
value class ProfileId(
        val value: ULong
)
