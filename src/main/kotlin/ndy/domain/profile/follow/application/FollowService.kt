package ndy.domain.profile.follow.application

import ndy.domain.profile.domain.ProfileId
import ndy.domain.profile.follow.domain.FollowRepository
import ndy.global.context.AuthenticatedUserContext
import ndy.global.context.ProfileIdContext

class FollowService(
    private val repository: FollowRepository
) {
    context (AuthenticatedUserContext)
    suspend fun follow(targetProfileId: ProfileId) {
        repository.save(profileId, targetProfileId)
    }

    context (AuthenticatedUserContext)
    suspend fun unfollow(targetProfileId: ProfileId) {
        repository.delete(profileId, targetProfileId)
    }

    context (AuthenticatedUserContext)
    suspend fun isFollowing(targetProfileId: ProfileId): Boolean {
        return repository.exists(profileId, targetProfileId)
    }

    context (AuthenticatedUserContext)
    fun isFollowingList(map: List<ProfileId>): List<Boolean> {
        return emptyList()
    }
}