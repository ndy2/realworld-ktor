package ndy.domain.profile.application

import ndy.context.UserIdContext
import ndy.domain.profile.domain.Bio
import ndy.domain.profile.domain.Image
import ndy.domain.profile.domain.ProfileRepository
import ndy.domain.profile.domain.Username
import ndy.domain.user.domain.User
import ndy.domain.user.domain.UserId
import ndy.exception.UsernameDuplicatedException
import ndy.util.mandatoryTransaction
import ndy.util.newTransaction
import ndy.util.notFound

class ProfileService(
    private val repository: ProfileRepository
) {

    context (UserIdContext)
    suspend fun register(username: String) = mandatoryTransaction {
        // validate
        if (checkUsernameDuplicated(username)) throw UsernameDuplicatedException(username)

        // action
        val profile = repository.save(UserId(userId), Username(username))

        // return
        ProfileResult(
            username = profile.username.value
        )
    }

    context (UserIdContext)
    suspend fun getByUserId() = mandatoryTransaction {
        // validate/action
        val profile = repository.findByUserId(UserId(userId)) ?: notFound<User>(userId)

        // return
        ProfileResult(
            username = profile.username.value,
            bio = profile.bio?.value,
            image = profile.image?.fullPath,
            following = false
        )
    }

    context (UserIdContext)
    suspend fun update(username: String?, bio: String?, image: String?) = mandatoryTransaction {
        // validate
        username?.let { if (checkUsernameDuplicated(it)) throw UsernameDuplicatedException(it) }

        // action/return
        repository.updateByUserId(
            UserId(userId),
            username?.let { Username(it) },
            bio?.let { Bio(it) },
            image?.let { Image.ofFullPath(it) },
        )
    }

    suspend fun checkUsernameDuplicated(username: String) = newTransaction {
        // action/return
        repository.existByUsername(Username(username))
    }
}

data class ProfileResult(
    val username: String,
    val bio: String? = null,
    val image: String? = null,
    val following: Boolean = false
)