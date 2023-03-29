package ndy.domain.profile.application

import ndy.context.UserIdContext
import ndy.domain.profile.domain.Bio
import ndy.domain.profile.domain.Image
import ndy.domain.profile.domain.ProfileRepository
import ndy.domain.profile.domain.Username
import ndy.domain.user.domain.UserId
import ndy.util.mandatoryTransaction

class ProfileService(
    private val repository: ProfileRepository
) {

    context (UserIdContext)
    suspend fun register(username: String) = mandatoryTransaction {
        val profile = repository.save(UserId(userId), Username(username))

        ProfileResult(username = profile.username.value)
    }

    context (UserIdContext)
    suspend fun getUsernameByUserId() = mandatoryTransaction {
        repository.findUsernameByUserId(UserId(userId))
    }

    context (UserIdContext)
    suspend fun update(username: String, bio: String, image: String) {
        repository.updateById(
            UserId(userId),
            Username(username),
            Bio(bio),
            Image.ofFullPath(image),
        )
    }
}

data class ProfileResult(
    val username: String,
    val bio: String? = null,
    val image: String? = null,
    val following: Boolean? = null
)