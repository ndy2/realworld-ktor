package ndy.domain.user.application

import ndy.domain.user.domain.Email
import ndy.domain.user.domain.Password
import ndy.domain.user.domain.UserRepository
import ndy.domain.user.domain.Username

class UserService(
    private val userRepository: UserRepository
) {
    suspend fun register(username: String, email: String, password: String): UserRegisterResult {
        //TODO - validate duplicated username or email

        userRepository.save(
            Username(username),
            Email(email),
            Password(password)
        )

        return UserRegisterResult(username, email)
    }
}

data class UserRegisterResult(
    val email: String,
    val username: String,
)