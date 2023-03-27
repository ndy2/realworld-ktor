package ndy.domain.profile.domain

interface ProfileRepository {

    suspend fun save(username: Username): Profile
    suspend fun findById(id: ULong): Profile?
}