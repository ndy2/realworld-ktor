package ndy.api.dto

import kotlinx.serialization.Serializable
import ndy.domain.user.application.UserResult

@Serializable
data class LoginRequest(
        val email: String,
        val password: String
)

@Serializable
data class RegistrationRequest(
        val username: String,
        val email: String,
        val password: String
)

@Serializable
data class UserUpdateRequest(
        val email: String?,
        val password: String?,
        val username: String?,
        val bio: String?,
        val image: String?
)

@Serializable
data class UserResponse(
        val email: String,
        val token: String?,
        val username: String,
        val bio: String?,
        val image: String?
) {
    companion object {
        fun ofResult(result: UserResult, token: String? = null) = UserResponse(
                email = result.email,
                token = result.token ?: token,
                username = result.username,
                bio = result.bio,
                image = result.image
        )
    }
}
