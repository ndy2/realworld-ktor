package ndy.domain.profile.follow.application

import ndy.context.AuthenticatedUserContext
import ndy.domain.profile.follow.domain.FollowRepository
import ndy.domain.user.domain.UserId

class FollowService(
    private val repository: FollowRepository
) {

    context (AuthenticatedUserContext)
    suspend fun follow(targetUserId: UserId) {
        repository.save(userId, targetUserId)
    }

    context (AuthenticatedUserContext)
    suspend fun unfollow(targetUserId: UserId) {
        repository.delete(userId, targetUserId)
    }

    context (AuthenticatedUserContext)
    suspend fun checkFollow(targetUserId: UserId): Boolean {
        return repository.exists(userId, targetUserId)
    }
}