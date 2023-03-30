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

        // 1. find user
        val user = repository.findUserByEmail(Email(email)) ?: authenticationFail("login failure")

        // 2. check password
        user.password.checkPassword(password, passwordVerifier)

        // 3. create token
        val token = JwtTokenService.createToken(user)

        // 4. get profile - TODO apply join query
        val profileResult = with(userIdContext(user.id)) { profileService.getByUserId() }

        // 4. return
        UserLoginResult(
            email = user.email.value,
            token = token,
            username = profileResult.username,
            bio = profileResult.bio,
            image = profileResult.image,
        )
    }

    suspend fun register(username: String, email: String, password: String) = newTransaction {
        log.info("register new user - username: $username, password : $password")

        // 1. save user
        val user = repository.save(
            Email(email),
            Password(password, passwordEncoder)
        )

        // 2. save profile
        with(userIdContext(user.id)) { profileService.register(username) }

        // 3. return
        UserRegisterResult(username, email)
    }

    context (AuthenticatedUserContext)
    suspend fun getById() = newTransaction {
        // 1. find user
        val foundUser = repository.findUserById(userId) ?: notFound<User>(userId.value)

        // 2. get profile - TODO apply join query
        val profileResult = with(userIdContext(userId)) {
            profileService.getByUserId()
        }

        // 3. return
        UserResult(
            email = foundUser.email.value,
            username = profileResult.username,
            bio = profileResult.bio,
            image = profileResult.image
        )
    }

    context (AuthenticatedUserContext)
    suspend fun update(
        email: String?, password: String?,
        username: String?, bio: String?, image: String?
    ) = newTransaction {
        // 1. update user
        repository.updateById(userId, email?.let { Email(it) }, password?.let { Password(it) })

        // 2. find user
        val foundUser = repository.findUserById(userId) ?: notFound<User>(userId.value)

        // 3. update profile and find it - TODO apply join query
        val profileResult = with(userIdContext(userId)) {
            profileService.update(username, bio, image)
            profileService.getByUserId()
        }

        // 4. combine result
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
    val token: String?,
    val username: String,
    val bio: String?,
    val image: String?,
)

data class UserResult(
    val email: String,
    val username: String,
    val bio: String?,
    val image: String?,
)