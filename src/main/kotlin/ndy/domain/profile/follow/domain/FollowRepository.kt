package ndy.domain.profile.follow.domain

import ndy.domain.profile.domain.ProfileId

interface FollowRepository {

    suspend fun save(followerId: ProfileId, followeeId: ProfileId)
    suspend fun delete(followerId: ProfileId, followeeId: ProfileId)
    suspend fun exists(followerId: ProfileId, followeeId: ProfileId): Boolean
    suspend fun existsList(followerId: ProfileId, followeeIds: List<ProfileId>): List<Boolean>
}
