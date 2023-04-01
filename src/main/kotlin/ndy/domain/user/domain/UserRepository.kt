package ndy.domain.user.domain

import ndy.domain.profile.domain.Profile

typealias UserWithProfile = Pair<User, Profile>

interface UserRepository {

    suspend fun save(email: Email, password: Password): User
    suspend fun findUserById(id: UserId): User?
    suspend fun findUserByEmail(email: Email): User?
    suspend fun updateById(id: UserId, email: Email?, password: Password?): Int
    suspend fun findUserByIdWithProfile(id: UserId): UserWithProfile?
    suspend fun findUserByEmailWithProfile(email: Email): UserWithProfile?
}