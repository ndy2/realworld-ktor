package ndy.infra.tables

import ndy.domain.profile.follow.domain.FollowRepository
import ndy.domain.user.domain.UserId

object FollowTable : FollowRepository {

    override suspend fun save(userId: UserId, targetUserId: UserId) {
        TODO("Not yet implemented")
    }

    override suspend fun delete(userId: UserId, targetUserId: UserId) {
        TODO("Not yet implemented")
    }

    override suspend fun exists(userId: UserId, targetUserId: UserId): Boolean {
        TODO("Not yet implemented")
    }
}