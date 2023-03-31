package ndy.api.dto

import kotlinx.serialization.Serializable
import ndy.domain.profile.application.ProfileResult

@Serializable
data class ProfileResponse(
    val username: String,
    val bio: String?,
    val image: String?,
    val following: Boolean,
) {
    companion object {
        fun ofResult(result: ProfileResult) = ProfileResponse(
            username = result.username,
            bio = result.bio,
            image = result.image,
            following = result.following
        )
    }
}
