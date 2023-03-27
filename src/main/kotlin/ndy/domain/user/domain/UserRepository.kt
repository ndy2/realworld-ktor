package ndy.domain.user.domain

interface UserRepository {
    suspend fun save(email: Email, password: Password): User

    suspend fun findUserById(id: ULong): User?

    suspend fun findUserByEmail(email: Email): User?
}