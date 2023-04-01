package ndy.domain.profile.application

import ndy.domain.profile.domain.*
import ndy.domain.profile.follow.application.FollowService
import ndy.domain.user.domain.User
import ndy.domain.user.domain.UserId
import ndy.global.context.AuthenticatedUserContext
import ndy.global.exception.UsernameDuplicatedException
import ndy.global.util.*

class ProfileService(
    private val repository: ProfileRepository,
    private val followService: FollowService,
) {
    suspend fun register(userId: UserId, username: String) = mandatoryTransaction {
        // validate
        if (checkUsernameDuplicated(username)) throw UsernameDuplicatedException(username)

        // action
        val profile = repository.save(userId, Username(username))

        // return
        ProfileResult.ofEntity(profile, false)
    }

    suspend fun getByUserId(userId: UserId) = mandatoryTransaction {
        // validate/action
        val profile = repository.findByUserId(userId) ?: notFound<User>(userId.value)

        // return
        ProfileResult.ofEntity(profile, false/* always used for current user */)
    }

    suspend fun update(userId: UserId, username: String?, bio: String?, image: String?) = mandatoryTransaction {
        // validate
        username?.let { if (checkUsernameDuplicated(it)) throw UsernameDuplicatedException(it) }

        // action/return
        repository.updateByUserId(
            userId,
            username?.let { Username(it) },
            bio?.let { Bio(it) },
            image?.let { Image.ofFullPath(it) },
        )
    }

    suspend fun checkUsernameDuplicated(username: String) = requiresNewTransaction {
        // action/return
        repository.existByUsername(Username(username))
    }

    context (AuthenticatedUserContext/* optional = true */)
    suspend fun getByUsername(username: String) = requiredTransaction {
        // validate - user exists & setup - find target userId
        val profile = Username(username)
            .run { repository.findProfileByUsername(this) ?: notFound(Profile::username, this) }

        // action - check following
        val following =
            if (authenticated) followService.isFollowing(targetProfileId = profile.id)
            else false

        // return
        ProfileResult.ofEntity(profile, following)
    }

    context (AuthenticatedUserContext)
    suspend fun follow(username: String) = requiresNewTransaction {
        // validate - user exists & setup - find target userId
        val profile = Username(username)
            .run { repository.findProfileByUsername(this) ?: notFound(Profile::username, this) }

        // action
        followService.follow(targetProfileId = profile.id)

        // return
        ProfileResult.ofEntity(profile, true)
    }

    context (AuthenticatedUserContext)
    suspend fun unfollow(username: String) = requiresNewTransaction {
        // validate - user exists & setup - find target userId
        val profile = Username(username)
            .run { repository.findProfileByUsername(this) ?: notFound(Profile::username, this) }

        // action
        followService.unfollow(targetProfileId = profile.id)

        // return
        ProfileResult.ofEntity(profile, false)
    }
}

