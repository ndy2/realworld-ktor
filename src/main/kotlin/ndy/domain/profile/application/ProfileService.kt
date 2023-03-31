package ndy.domain.profile.application

import ndy.domain.profile.domain.*
import ndy.domain.profile.follow.application.FollowService
import ndy.domain.user.domain.User
import ndy.domain.user.domain.UserId
import ndy.global.context.AuthenticatedUserContext
import ndy.global.context.UserIdContext
import ndy.global.exception.UsernameDuplicatedException
import ndy.global.util.mandatoryTransaction
import ndy.global.util.requiresNewTransaction
import ndy.global.util.notFound
import ndy.global.util.notFoundField

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
        ProfileResult.ofEntity(profile, false)
    }

    context (UserIdContext)
    suspend fun getByUserId() = mandatoryTransaction {
        // validate/action
        val profile = repository.findByUserId(UserId(userId)) ?: notFound<User>(userId)

        // return
        ProfileResult.ofEntity(profile, false/* always used for current user */)
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

    suspend fun checkUsernameDuplicated(username: String) = requiresNewTransaction {
        // action/return
        repository.existByUsername(Username(username))
    }

    context (AuthenticatedUserContext/* optional = true */)
    suspend fun getByUsername(username: String) = requiresNewTransaction {
        // validate - user exists & setup - find target userId
        val profile = Username(username)
            .run { repository.findProfileByUsername(this) ?: notFoundField(Profile::username, this) }

        // action - check following
        val following =
            if (userIdNullable == null) false // false if not authenticated
            else followService.isFollowing(targetProfileId = profile.id)

        // return
        ProfileResult.ofEntity(profile, following)
    }

    context (AuthenticatedUserContext)
    suspend fun follow(username: String) = requiresNewTransaction {
        // validate - user exists & setup - find target userId
        val profile = Username(username)
            .run { repository.findProfileByUsername(this) ?: notFoundField(Profile::username, this) }

        // action
        followService.follow(targetProfileId = profile.id)

        // return
        ProfileResult.ofEntity(profile, true)
    }

    context (AuthenticatedUserContext)
    suspend fun unfollow(username: String) = requiresNewTransaction {
        // validate - user exists & setup - find target userId
        val profile = Username(username)
            .run { repository.findProfileByUsername(this) ?: notFoundField(Profile::username, this) }

        // action
        followService.unfollow(targetProfileId = profile.id)

        // return
        ProfileResult.ofEntity(profile, false)
    }
}

