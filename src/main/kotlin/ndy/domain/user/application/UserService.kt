package ndy.domain.user.application

import ndy.domain.profile.application.ProfileService
import ndy.domain.user.domain.*
import ndy.global.context.AuthenticatedUserContext
import ndy.global.context.userIdContext
import ndy.global.util.authenticationFail
import ndy.global.util.newTransaction
import ndy.global.util.notFound

class UserService(
    private val repository: UserRepository,
    private val profileService: ProfileService,
    private val passwordEncoder: PasswordEncoder,
    private val passwordVerifier: PasswordVerifier,
) {
    suspend fun login(email: String, password: String) = newTransaction {
        // 1. find user
        val user = repository.findUserByEmailWithProfile(Email(email)) ?: authenticationFail("login failure")
        require(user.profile != null)

        // 2. check password
        user.password.checkPassword(password, passwordVerifier)

        // 3. create token
        val token = JwtTokenService.createToken(user)

        // 4. return
        UserResult(
            email = user.email.value,
            token = token,
            username = user.profile.username.value,
            bio = user.profile.bio?.value,
            image = user.profile.image?.fullPath
        )
    }

    suspend fun register(username: String, email: String, password: String) = newTransaction {
        // 1. save user
        val user = repository.save(
            Email(email),
            Password(password, passwordEncoder)
        )

        // 2. save profile
        with(userIdContext(user.id)) { profileService.register(username) }

        // 3. return
        UserResult(
            email = email,
            username = username,
            token = null,
            bio = null,
            image = null,
        )
    }

    context (AuthenticatedUserContext)
    suspend fun getById() = newTransaction {
        // 1. find user with profile
        val foundUser = repository.findUserByIdWithProfile(userId) ?: notFound<User>(userId.value)
        require(foundUser.profile != null)

        // 2. return
        UserResult(
            email = foundUser.email.value,
            username = foundUser.profile.username.value,
            token = null, /* would be filled @routs */
            bio = foundUser.profile.bio?.value,
            image = foundUser.profile.image?.fullPath
        )
    }

    context (AuthenticatedUserContext)
    suspend fun update(
        email: String?, password: String?,
        username: String?, bio: String?, image: String?
    ) = newTransaction {
        // 1. get origUser
        val origUser = getById()

        // 2. update user
        repository.updateById(userId, email?.let { Email(it) }, password?.let { Password(it) })

        // 3. update profile
        with(userIdContext()) { profileService.update(username, bio, image) }

        // 4. return
        UserResult(
            email = email ?: origUser.email,
            username = username ?: origUser.username,
            token = null, /* would be filled @routs */
            bio = bio ?: origUser.bio,
            image = image ?: origUser.image,
        )
    }
}
