package ndy.domain.profile.application

import ndy.domain.profile.domain.ProfileRepository
import ndy.domain.profile.domain.Username
import ndy.domain.user.domain.UserId
import ndy.util.mandatoryTransaction

class ProfileService(
    private val repository: ProfileRepository
) {
    suspend fun register(userId: ULong, username: String) = mandatoryTransaction {
        val profile = repository.save(UserId(userId), Username(username))

        ProfileResult(username = profile.username.value)
    }

    suspend fun getUsernameByUserId(userId: ULong) = mandatoryTransaction {
        repository.findUsernameByUserId(UserId(userId))
    }
}

data class ProfileResult(
    val username: String,
    val bio: String? = null,
    val image: String? = null,
    val following: Boolean? = null
)