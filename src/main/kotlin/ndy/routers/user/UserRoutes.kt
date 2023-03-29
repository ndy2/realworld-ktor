package ndy.routers.user

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import ndy.domain.user.application.UserService
import ndy.util.authenticatedGet
import ndy.util.created
import ndy.util.ok
import org.koin.ktor.ext.inject

fun Route.userRouting() {
    val userService by inject<UserService>()

    post<Users> {
        val request = call.receive<Map<String, RegistrationRequest>>()["user"]!!

        val result = userService.register(
            username = request.username,
            email = request.email,
            password = request.password,
        )

        val response = UserResponse(
            email = result.email,
            username = result.username
        )
        call.created(mapOf("user" to response))
    }

    post<Users.Login> {
        val request = call.receive<Map<String, LoginRequest>>()["user"]!!

        val result = userService.login(
            email = request.email,
            password = request.password
        )

        val response = UserResponse(
            email = result.email,
            token = result.token,
            username = result.username,
            bio = result.bio,
            image = result.image
        )
        call.ok(mapOf("user" to response))
    }

    authenticatedGet<User> {
        val result = userService.getById()

        val response = UserResponse(
            email = result.email,
            username = result.username
        )
        call.ok(mapOf("user" to response))
    }
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
data class UserResponse(
    val email: String,
    val token: String? = null,
    val username: String,
    val bio: String? = null,
    val image: String? = null,
)