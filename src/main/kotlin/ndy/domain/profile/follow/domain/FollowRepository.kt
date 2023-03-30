package ndy.domain.profile.follow.domain

import ndy.domain.user.domain.UserId

interface FollowRepository {

    suspend fun save(userId: UserId, targetUserId: UserId)
    suspend fun delete(userId: UserId, targetUserId: UserId)
    suspend fun exists(userId: UserId, targetUserId: UserId): Boolean

}