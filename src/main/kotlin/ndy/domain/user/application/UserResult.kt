package ndy.domain.user.application

import ndy.domain.profile.domain.Profile
import ndy.domain.user.domain.User

data class UserResult(
        val email: String,
        val token: String?,
        val username: String,
        val bio: String?,
        val image: String?
) {
    companion object {
        fun from(user: User, profile: Profile, token: String?) = UserResult(
                email = user.email.value,
                token = token,
                username = profile.username.value,
                bio = profile.bio?.value,
                image = profile.image?.fullPath
        )
    }
}
