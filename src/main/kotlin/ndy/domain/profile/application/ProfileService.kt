package ndy.domain.profile.application

import ndy.context.UserIdContext
import ndy.domain.profile.domain.Bio
import ndy.domain.profile.domain.Image
import ndy.domain.profile.domain.ProfileRepository
import ndy.domain.profile.domain.Username
import ndy.domain.user.domain.UserId
import ndy.util.mandatoryTransaction
import ndy.util.notFound

class ProfileService(
    private val repository: ProfileRepository
) {

    context (UserIdContext)
    suspend fun register(username: String) = mandatoryTransaction {
        val profile = repository.save(UserId(userId), Username(username))

        ProfileResult(username = profile.username.value)
    }

    context (UserIdContext)
    suspend fun getByUserId() = mandatoryTransaction {
        val profile = repository.findByUserId(UserId(userId)) ?: notFound()

        ProfileResult(
            username = profile.username.value,
            bio = profile.bio?.value,
            image = profile.image?.fullPath,
            following = false
        )
    }

    context (UserIdContext)
    suspend fun update(username: String?, bio: String?, image: String?) = mandatoryTransaction {
        repository.updateByUserId(
            UserId(userId),
            username?.let { Username(it) },
            bio?.let { Bio(it) },
            image?.let { Image.ofFullPath(it) },
        )
    }
}

data class ProfileResult(
    val username: String,
    val bio: String? = null,
    val image: String? = null,
    val following: Boolean = false
)