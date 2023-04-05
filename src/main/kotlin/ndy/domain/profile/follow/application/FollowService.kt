package ndy.domain.profile.follow.application

import ndy.domain.profile.domain.ProfileId
import ndy.domain.profile.follow.domain.FollowRepository
import ndy.global.exception.ValidationException
import ndy.global.security.Principal
import ndy.ktor.context.auth.AuthenticationContext

class FollowService(
        private val repository: FollowRepository
) {
    context (AuthenticationContext<Principal>)
    suspend fun follow(targetProfileId: ProfileId) {
        if (principal.profileId == targetProfileId) throw ValidationException("self follow is not allowed")
        repository.save(principal.profileId, targetProfileId)
    }

    context (AuthenticationContext<Principal>)
    suspend fun unfollow(targetProfileId: ProfileId) {
        if (principal.profileId == targetProfileId) throw ValidationException("self unfollow is not allowed")
        repository.delete(principal.profileId, targetProfileId)
    }

    context (AuthenticationContext<Principal>)
    suspend fun isFollowing(targetProfileId: ProfileId): Boolean {
        if (principal.profileId == targetProfileId) return false
        return repository.exists(principal.profileId, targetProfileId)
    }

    context (AuthenticationContext<Principal>)
    suspend fun isFollowingList(targetProfileIds: List<ProfileId>): List<Boolean> {
        return repository.existsList(principal.profileId, targetProfileIds)
    }
}
