package ndy.domain.profile.domain

interface ProfileRepository {

    suspend fun save(userId: ULong, username: Username): Profile
    suspend fun findById(id: ProfileId): Profile?
    suspend fun findUsernameByUserId(userId: ULong): String
}