package ndy.domain.profile.follow.domain

import ndy.domain.user.domain.UserId

interface FollowRepository {

    suspend fun save(followerId: UserId, followeeId: UserId)
    suspend fun delete(followerId: UserId, followeeId: UserId)
    suspend fun exists(followerId: UserId, followeeId: UserId): Boolean
}