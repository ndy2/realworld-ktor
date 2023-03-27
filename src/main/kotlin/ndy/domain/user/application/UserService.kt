package ndy.domain.user.application

import ndy.domain.user.domain.*
import ndy.util.fail

class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val passwordVerifier: PasswordVerifier,
) {
    suspend fun register(username: String, email: String, password: String): UserRegisterResult {
        userRepository.save(
            Username(username),
            Email(email),
            Password(password, passwordEncoder)
        )

        return UserRegisterResult(username, email)
    }

    suspend fun login(email: String, password: String): UserLoginResult {
        // 1. email 로 사용자 조회
        val user = userRepository.findUserByEmail(Email(email)) ?: fail("login failure")

        // 2. password 검증
        user.password.checkPassword(password, passwordVerifier)

        // 3. token 생성


        // 4. 응답
        return UserLoginResult("", "", "")
    }
}

data class UserRegisterResult(
    val username: String,
    val email: String,
)

data class UserLoginResult(
    val email: String,
    val token: String? = null,
    val username: String,
    val bio: String? = null,
    val image: String? = null,
)