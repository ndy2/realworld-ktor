package ndy.domain.user.application

import ndy.domain.user.domain.*
import ndy.util.fail
import ndy.util.newTransaction

class UserService(
    private val repository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val passwordVerifier: PasswordVerifier,
) {
    suspend fun login(email: String, password: String) = newTransaction {
        // 1. email 로 사용자 조회
        val user = repository.findUserByEmail(Email(email)) ?: fail("login failure")

        // 2. password 검증
        user.password.checkPassword(password, passwordVerifier)

        // 3. token 생성
        val token = JwtTokenService.createToken(user)

        // 4. 응답
        UserLoginResult(
            email = user.email.value,
            token = token,
            username = user.username.value,
            bio = null,
            image = null,
        )
    }

    suspend fun register(username: String, email: String, password: String) = newTransaction {
        repository.save(
            Username(username),
            Email(email),
            Password(password, passwordEncoder)
        )

        UserRegisterResult(username, email)
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