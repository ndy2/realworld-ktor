package ndy.domain.profile.application

import ndy.context.AuthenticatedUserContext
import ndy.context.UserIdContext
import ndy.domain.profile.domain.*
import ndy.domain.profile.follow.application.FollowService
import ndy.domain.user.domain.User
import ndy.domain.user.domain.UserId
import ndy.exception.UsernameDuplicatedException
import ndy.util.*

class ProfileService(
    private val repository: ProfileRepository,
    private val followService: FollowService,
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

    context (AuthenticatedUserContext/* optional = true */)
    suspend fun getByUsername(username: String) = newTransaction {
        // validate - user exists & setup - find target userId
        val profile = Username(username)
            .run { repository.findByUsername(this) ?: notFoundField(Profile::username, this) }

        // action - check following
        val following =
            if (userIdNullable == null) false // false if not authenticated
            else followService.checkFollow(profile.userId)

        // return
        ProfileResult(
            username = profile.username.value,
            bio = profile.bio?.value,
            image = profile.image?.fullPath,
            following = following
        )
    }

    context (AuthenticatedUserContext)
    suspend fun follow(username: String) = newTransaction {
        // validate - user exists & setup - find target userId
        val profile = Username(username)
            .run { repository.findByUsername(this) ?: notFoundField(Profile::username, this) }

        // action
        followService.follow(profile.userId)

        // return
        ProfileResult(
            username = profile.username.value,
            bio = profile.bio?.value,
            image = profile.image?.fullPath,
            following = true
        )
    }

    context (AuthenticatedUserContext)
    suspend fun unfollow(username: String) = newTransaction {
        // validate - user exists & setup - find target userId
        val profile = Username(username)
            .run { repository.findByUsername(this) ?: notFoundField(Profile::username, this) }

        // action
        followService.unfollow(profile.userId)

        // return
        ProfileResult(
            username = profile.username.value,
            bio = profile.bio?.value,
            image = profile.image?.fullPath,
            following = false
        )
    }
}

data class ProfileResult(
    val username: String,
    val bio: String? = null,
    val image: String? = null,
    val following: Boolean = false
)