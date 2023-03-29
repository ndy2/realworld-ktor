package ndy.domain.user.application

import ndy.context.AuthenticatedUserContext
import ndy.context.LoggingContext
import ndy.context.userIdContext
import ndy.domain.profile.application.ProfileService
import ndy.domain.user.domain.*
import ndy.util.authenticationFail
import ndy.util.newTransaction
import ndy.util.notFound

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
        val user = repository.findUserByEmail(Email(email)) ?: authenticationFail("login failure")

        // 2. password 검증
        user.password.checkPassword(password, passwordVerifier)

        // 3. token 생성
        val token = JwtTokenService.createToken(user)

        // 4. profile.username 조회
        val username = with(userIdContext(user.id)) { profileService.getByUserId() }.username

        // 4. 응답
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
        with(userIdContext(user.id)) { profileService.register(username) }

        // 3. 응답
        UserRegisterResult(username, email)
    }

    context (AuthenticatedUserContext)
    suspend fun getById() = newTransaction {
        // find user in user table
        val foundUser = repository.findUserById(userId) ?: notFound()

        // get profile from profileService
        val profileResult = with(userIdContext(userId)) {
            profileService.getByUserId()
        }

        // combine results
        UserLoginResult(
            email = foundUser.email.value,
            username = profileResult.username,
            token = null,
            bio = profileResult.bio,
            image = profileResult.image
        )
    }

    context (AuthenticatedUserContext)
    suspend fun update(
        email: String?,
        password: String?,
        username: String?,
        bio: String?,
        image: String?
    ): UserResult = newTransaction {
        // update user table and find it
        repository.updateById(
            userId,
            email?.let { Email(it) },
            password?.let { Password(it) }
        )
        val foundUser = repository.findUserById(userId) ?: notFound()

        // update profile table and find it
        val profileResult = with(userIdContext(userId)) {
            profileService.update(username, bio, image)
            profileService.getByUserId()
        }

        // combine result
        UserResult(
            email = foundUser.email.value,
            username = profileResult.username,
            bio = profileResult.bio,
            image = profileResult.image
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

data class UserResult(
    val email: String,
    val username: String,
    val bio: String? = null,
    val image: String? = null,
)