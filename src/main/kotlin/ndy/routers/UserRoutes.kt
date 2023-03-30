package ndy.routers

import io.ktor.server.application.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import ndy.domain.user.application.UserLoginResult
import ndy.domain.user.application.UserRegisterResult
import ndy.domain.user.application.UserResult
import ndy.domain.user.application.UserService
import ndy.resources.User
import ndy.resources.Users
import ndy.util.*
import org.koin.ktor.ext.inject

fun Route.userRouting() {
    val userService by inject<UserService>()

    post<Users> {
        // bind
        val request = call.extract<RegistrationRequest>("user")

        // action
        val result = userService.register(
            username = request.username,
            email = request.email,
            password = request.password,
        )

        // return
        val response = UserResponse.ofRegisterResult(result)
        call.created(mapOf("user" to response))
    }

    post<Users.Login> {
        // bind
        val request = call.extract<LoginRequest>("user")

        // action
        val result = userService.login(
            email = request.email,
            password = request.password
        )

        // return
        val response = UserResponse.ofLoginResult(result)
        call.okUser(response)
    }

    authenticatedGet<User> {
        // action
        val result = userService.getById()

        // return
        val response = UserResponse.ofResult(result, call.token())
        call.okUser(response)
    }

    authenticatedPut<User> {
        // bind
        val request = call.extract<UserUpdateRequest>("user")

        // action
        val result = userService.update(
            email = request.email,
            password = request.password,
            username = request.username,
            bio = request.bio,
            image = request.image,
        )

        // return
        val response = UserResponse.ofResult(result, call.token())
        call.okUser(response)
    }
}

private suspend inline fun <reified T : Any> ApplicationCall.okUser(response: T) {
    ok(mapOf("user" to response))
}

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class RegistrationRequest(
    val username: String,
    val email: String,
    val password: String,
)

@Serializable
data class UserUpdateRequest(
    val email: String?,
    val password: String?,
    val username: String?,
    val bio: String?,
    val image: String?,
)

@Serializable
data class UserResponse(
    val email: String,
    val token: String?,
    val username: String,
    val bio: String?,
    val image: String?,
) {
    companion object {
        fun ofResult(result: UserResult, token: String?) = UserResponse(
            email = result.email,
            token = token,
            username = result.username,
            bio = result.bio,
            image = result.image,
        )

        fun ofLoginResult(result: UserLoginResult) = UserResponse(
            email = result.email,
            token = result.token,
            username = result.username,
            bio = result.bio,
            image = result.image,
        )

        fun ofRegisterResult(result: UserRegisterResult) = UserResponse(
            email = result.email,
            token = null,
            username = result.username,
            bio = null,
            image = null,
        )

    }
}