package ndy.domain.user.application

import ndy.context.AuthenticatedUserContext
import ndy.context.LoggingContext
import ndy.domain.profile.application.ProfileService
import ndy.domain.user.domain.*
import ndy.util.fail
import ndy.util.newTransaction

context (LoggingContext)
class UserService(
    private val repository: UserRepository,
    private val profileService: ProfileService,
    private val passwordEncoder: PasswordEncoder,
    private val passwordVerifier: PasswordVerifier,
) {
    suspend fun login(email: String, password: String) = newTransaction {
        log.info("login request - email : $email")

        // 1. email 로 사용자 조회
        val user = repository.findUserByEmail(Email(email)) ?: fail("login failure")

        // 2. password 검증
        user.password.checkPassword(password, passwordVerifier)

        // 3. token 생성
        val token = JwtTokenService.createToken(user)

        // 4. profile.username 조회
        val username = profileService.getUsernameByUserId(user.id.value)

        // 4. 응답
        log.info("login done - email : $email")
        UserLoginResult(
            email = user.email.value,
            token = token,
            username = username,
            bio = null,
            image = null,
        )
    }

    suspend fun register(username: String, email: String, password: String) = newTransaction {
        // 1. user 저장
        log.info("register new user - username: $username, password : $password")
        val user = repository.save(
            Email(email),
            Password(password, passwordEncoder)
        )

        // 2. profile 저장
        profileService.register(user.id.value, username)

        // 3. 응답
        UserRegisterResult(username, email)
    }

    context (AuthenticatedUserContext)
    suspend fun getById() = newTransaction {
        val user = repository.findUserById(userId) ?: fail("no such user")

        UserLoginResult(
            email = user.email.value,
            username = "todo"
        )
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