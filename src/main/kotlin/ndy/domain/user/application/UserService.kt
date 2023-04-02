package ndy.domain.user.application

import ndy.domain.profile.application.ProfileService
import ndy.domain.user.domain.*
import ndy.global.context.AuthenticatedUserContext
import ndy.global.util.authenticationFail
import ndy.global.util.notFound
import ndy.global.util.transactional

class UserService(
    private val repository: UserRepository,
    private val profileService: ProfileService,
    private val passwordEncoder: PasswordEncoder,
    private val passwordVerifier: PasswordVerifier,
) {
    suspend fun login(email: String, password: String) = transactional {
        // 1. find user
        val (user, profile) = repository.findUserByEmailWithProfile(Email(email)) ?: authenticationFail("login failure")

        // 2. check password
        user.password.checkPassword(password, passwordVerifier)

        // 3. create token
        val token = JwtTokenService.createToken(user, profile)

        // 4. return
        UserResult.from(user, profile, token)
    }

    suspend fun register(username: String, email: String, password: String) = transactional {
        // 1. create User
        val user = User(
            email = Email(email),
            password = Password(password, passwordEncoder)
        )

        // 2. save user
        val savedUser = repository.save(user)

        // 3. save profile
        profileService.register(savedUser.id, username)

        // 4. return
        UserResult(
            email = email,
            token = null,
            username = username,
            bio = null,
            image = null,
        )
    }

    context (AuthenticatedUserContext)
    suspend fun getById() = transactional {
        // 1. find user with profile
        val (user, profile) = repository.findUserByIdWithProfile(userId) ?: notFound<User>(userId.value)

        // 2. return
        UserResult.from(user, profile, null)
    }

    context (AuthenticatedUserContext)
    suspend fun update(
        email: String?, password: String?,
        username: String?, bio: String?, image: String?
    ) = transactional {
        // 1. get origUser
        val origUser = getById()

        // 2. update user
        repository.updateById(userId, email?.let { Email(it) }, password?.let { Password(it) })

        // 3. update profile
        profileService.update(userId, username, bio, image)

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
