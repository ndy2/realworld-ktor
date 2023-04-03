package ndy.api.dto

import ndy.domain.profile.application.ProfileResult

import kotlinx.serialization.Serializable

@Serializable
data class ProfileResponse(
    val username: String,
    val bio: String?,
    val image: String?,
    val following: Boolean
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
