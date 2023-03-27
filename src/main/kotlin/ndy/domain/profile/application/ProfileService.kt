package ndy.domain.profile.application

import ndy.domain.profile.domain.ProfileRepository
import ndy.domain.profile.domain.Username
import ndy.util.mandatoryTransaction

class ProfileService(
    private val repository: ProfileRepository
) {
    suspend fun register(username: String) = mandatoryTransaction {
        val profile = repository.save(Username(username))

        ProfileResult(username = profile.username.value)
    }
}

data class ProfileResult(
    val username: String,
    val bio: String? = null,
    val image: String? = null,
    val following: Boolean? = null
)