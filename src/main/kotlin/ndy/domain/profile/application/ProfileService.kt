package ndy.domain.profile.application

import ndy.domain.profile.domain.Bio
import ndy.domain.profile.domain.Image
import ndy.domain.profile.domain.Profile
import ndy.domain.profile.domain.ProfileRepository
import ndy.domain.profile.domain.Username
import ndy.domain.profile.follow.application.FollowService
import ndy.domain.user.domain.User
import ndy.domain.user.domain.UserId
import ndy.global.exception.UsernameDuplicatedException
import ndy.global.security.AuthenticationContext
import ndy.global.util.Propagation.MANDATORY
import ndy.global.util.notFound
import ndy.global.util.transactional

class ProfileService(
        private val repository: ProfileRepository,
        private val followService: FollowService
) {
    suspend fun register(userId: UserId, username: String) = transactional(MANDATORY) {
        // 1. validate
        if (checkUsernameDuplicated(username)) throw UsernameDuplicatedException(username)

        // 2. action
        val profile = repository.save(userId, Username(username))

        // 3. return
        ProfileResult.from(profile, false)
    }

    suspend fun getByUserId(userId: UserId) = transactional(MANDATORY) {
        // 1. validate/action
        val profile = repository.findByUserId(userId) ?: notFound<User>(userId.value)

        // 2. return
        ProfileResult.from(profile, false/* always used for current user */)
    }

    suspend fun update(userId: UserId, username: String?, bio: String?, image: String?) = transactional(MANDATORY) {
        // 1. validate
        username?.let { if (checkUsernameDuplicated(it)) throw UsernameDuplicatedException(it) }

        // 2. action/return
        repository.updateByUserId(
                userId,
                username?.let { Username(it) },
                bio?.let { Bio(it) },
                image?.let { Image.ofFullPath(it) }
        )
    }

    suspend fun checkUsernameDuplicated(username: String) = transactional {
        // 1. action/return
        repository.existByUsername(Username(username))
    }

    context (AuthenticationContext/* optional = true */)
    suspend fun getByUsername(username: String) = transactional {
        // 1. validate - user exists & setup - find target userId
        val profile = Username(username)
                .run { repository.findProfileByUsername(this) ?: notFound(Profile::username, this) }

        // 2. action - check following
        val following =
                if (authenticated) {
                    followService.isFollowing(targetProfileId = profile.id)
                } else {
                    false
                }

        // 3. return
        ProfileResult.from(profile, following)
    }

    context (AuthenticationContext)
    suspend fun follow(username: String) = transactional {
        // 1. validate - user exists & setup - find target userId
        val profile = Username(username)
                .run { repository.findProfileByUsername(this) ?: notFound(Profile::username, this) }

        // 2. action
        followService.follow(targetProfileId = profile.id)

        // 3. return
        ProfileResult.from(profile, true)
    }

    context (AuthenticationContext)
    suspend fun unfollow(username: String) = transactional {
        // 1. validate - user exists & setup - find target userId
        val profile = Username(username)
                .run { repository.findProfileByUsername(this) ?: notFound(Profile::username, this) }

        // 2. action
        followService.unfollow(targetProfileId = profile.id)

        // 3. return
        ProfileResult.from(profile, false)
    }
}
