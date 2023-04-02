package ndy.domain.profile.application

import ndy.domain.profile.domain.*
import ndy.domain.profile.follow.application.FollowService
import ndy.domain.user.domain.User
import ndy.domain.user.domain.UserId
import ndy.global.context.AuthenticatedUserContext
import ndy.global.exception.UsernameDuplicatedException
import ndy.global.util.Propagation.MANDATORY
import ndy.global.util.notFound
import ndy.global.util.transactional

class ProfileService(
    private val repository: ProfileRepository,
    private val followService: FollowService,
) {
    suspend fun register(userId: UserId, username: String) = transactional(MANDATORY) {
        // validate
        if (checkUsernameDuplicated(username)) throw UsernameDuplicatedException(username)

        // action
        val profile = repository.save(userId, Username(username))

        // return
        ProfileResult.from(profile, false)
    }

    suspend fun getByUserId(userId: UserId) = transactional(MANDATORY) {
        // validate/action
        val profile = repository.findByUserId(userId) ?: notFound<User>(userId.value)

        // return
        ProfileResult.from(profile, false/* always used for current user */)
    }

    suspend fun update(userId: UserId, username: String?, bio: String?, image: String?) = transactional(MANDATORY) {
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

    suspend fun checkUsernameDuplicated(username: String) = transactional {
        // action/return
        repository.existByUsername(Username(username))
    }

    context (AuthenticatedUserContext/* optional = true */)
    suspend fun getByUsername(username: String) = transactional {
        // validate - user exists & setup - find target userId
        val profile = Username(username)
            .run { repository.findProfileByUsername(this) ?: notFound(Profile::username, this) }

        // action - check following
        val following =
            if (authenticated) followService.isFollowing(targetProfileId = profile.id)
            else false

        // return
        ProfileResult.from(profile, following)
    }

    context (AuthenticatedUserContext)
    suspend fun follow(username: String) = transactional {
        // validate - user exists & setup - find target userId
        val profile = Username(username)
            .run { repository.findProfileByUsername(this) ?: notFound(Profile::username, this) }

        // action
        followService.follow(targetProfileId = profile.id)

        // return
        ProfileResult.from(profile, true)
    }

    context (AuthenticatedUserContext)
    suspend fun unfollow(username: String) = transactional {
        // validate - user exists & setup - find target userId
        val profile = Username(username)
            .run { repository.findProfileByUsername(this) ?: notFound(Profile::username, this) }

        // action
        followService.unfollow(targetProfileId = profile.id)

        // return
        ProfileResult.from(profile, false)
    }
}

