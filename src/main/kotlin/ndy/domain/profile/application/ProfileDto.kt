package ndy.domain.profile.application

import ndy.domain.profile.domain.Profile

data class ProfileResult(
    val id: ULong,
    val username: String,
    val bio: String?,
    val image: String?,
    val following: Boolean,
) {
    companion object {
        fun ofEntity(entity: Profile, following: Boolean) = ProfileResult(
            id = entity.id.value,
            username = entity.username.value,
            bio = entity.bio?.value,
            image = entity.image?.fullPath,
            following = following
        )
    }
}