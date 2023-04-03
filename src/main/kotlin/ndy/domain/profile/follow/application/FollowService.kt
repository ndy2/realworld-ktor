package ndy.domain.profile.follow.application

import ndy.domain.profile.domain.ProfileId
import ndy.domain.profile.follow.domain.FollowRepository
import ndy.global.context.AuthenticatedUserContext
import ndy.global.exception.ValidationException

class FollowService(
    private val repository: FollowRepository
) {
    context (AuthenticatedUserContext)
    suspend fun follow(targetProfileId: ProfileId) {
        if (profileId == targetProfileId) throw ValidationException("self follow is not allowed")
        repository.save(profileId, targetProfileId)
    }

    context (AuthenticatedUserContext)
    suspend fun unfollow(targetProfileId: ProfileId) {
        if (profileId == targetProfileId) throw ValidationException("self unfollow is not allowed")
        repository.delete(profileId, targetProfileId)
    }

    context (AuthenticatedUserContext)
    suspend fun isFollowing(targetProfileId: ProfileId): Boolean {
        if (profileId == targetProfileId) return false
        return repository.exists(profileId, targetProfileId)
    }

    context (AuthenticatedUserContext)
    suspend fun isFollowingList(targetProfileIds: List<ProfileId>): List<Boolean> {
        return repository.existsList(profileId, targetProfileIds)
    }
}
