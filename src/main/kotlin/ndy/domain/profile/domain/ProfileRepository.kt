package ndy.domain.profile.domain

import ndy.domain.user.domain.UserId

interface ProfileRepository {

    suspend fun save(userId: UserId, username: Username): Profile
    suspend fun findById(id: ProfileId): Profile?
    suspend fun findByUserId(userId: UserId): Profile?
    suspend fun updateByUserId(userId: UserId, username: Username?, bio: Bio?, image: Image?): Int
    suspend fun existByUsername(username: Username): Boolean
    suspend fun findByUsername(username: Username): Profile?
}