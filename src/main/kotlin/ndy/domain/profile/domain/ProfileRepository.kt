package ndy.domain.profile.domain

import ndy.domain.user.domain.UserId

interface ProfileRepository {

    suspend fun save(userId: UserId, username: Username): Profile
    suspend fun findById(id: ProfileId): Profile?
    suspend fun findUsernameByUserId(userId: UserId): String
}