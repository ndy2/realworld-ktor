package ndy.domain.profile.application

import ndy.context.AuthenticatedUserContext
import ndy.domain.user.domain.UserId

class FollowService {

    context (AuthenticatedUserContext)
    suspend fun follow(targetUserId: UserId) {

    }

    context (AuthenticatedUserContext)
    suspend fun unfollow(targetUserId: UserId) {

    }

    context (AuthenticatedUserContext)
    suspend fun checkFollow(targetUserId: UserId): Boolean {
        return true
    }
}