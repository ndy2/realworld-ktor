package ndy.domain.user.application

import ndy.domain.profile.application.ProfileService
import ndy.domain.user.domain.Email
import ndy.domain.user.domain.Password
import ndy.domain.user.domain.PasswordEncoder
import ndy.domain.user.domain.PasswordVerifier
import ndy.domain.user.domain.User
import ndy.domain.user.domain.UserRepository
import ndy.global.security.Principal
import ndy.global.util.authenticationFail
import ndy.global.util.notFound
import ndy.global.util.transactional
import ndy.ktor.context.auth.AuthenticationContext

class UserService(
        private val repository: UserRepository,
        private val profileService: ProfileService,
        private val passwordEncoder: PasswordEncoder,
        private val passwordVerifier: PasswordVerifier
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
                image = null
        )
    }

    context (AuthenticationContext<Principal>)
    suspend fun getById() = transactional {
        // 1. find user with profile
        val (user, profile) = repository.findUserByIdWithProfile(principal.userId)
                ?: notFound<User>(principal.userId.value)

        // 2. return
        UserResult.from(user, profile, null)
    }

    context (AuthenticationContext<Principal>)
    suspend fun update(
            email: String?,
            password: String?,
            username: String?,
            bio: String?,
            image: String?
    ) = transactional {
        // 1. get origUser
        val origUser = getById()

        // 2. update user
        repository.updateById(principal.userId, email?.let { Email(it) }, password?.let { Password(it) })

        // 3. update profile
        profileService.update(principal.userId, username, bio, image)

        // 4. return
        UserResult(
                email = email ?: origUser.email,
                username = username ?: origUser.username,
                token = null, /* would be filled @routs */
                bio = bio ?: origUser.bio,
                image = image ?: origUser.image
        )
    }
}
