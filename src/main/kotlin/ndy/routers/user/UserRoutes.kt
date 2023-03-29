package ndy.routers.user

import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import ndy.domain.user.application.UserService
import ndy.util.created
import ndy.util.forward
import ndy.util.getWithAuthenticatedUser
import ndy.util.ok
import org.koin.ktor.ext.inject

fun Route.userRouting() {
    val userService by inject<UserService>()

    route("/users") {
        // authentication
        post("/login") {
            // binding
            val request = call.receive<Map<String, LoginRequest>>()["user"]!!

            // invoke service
            val result = userService.login(
                email = request.email,
                password = request.password
            )

            // response
            val response = UserResponse(
                email = result.email,
                token = result.token,
                username = result.username,
                bio = result.bio,
                image = result.image
            )
            call.ok(mapOf("user" to response))
        }

        // registration
        post {
            // binding
            val request = call.receive<Map<String, RegistrationRequest>>()["user"]!!

            // invoke service
            val result = userService.register(
                username = request.username,
                email = request.email,
                password = request.password,
            )

            // response
            val response = UserResponse(
                email = result.email,
                username = result.username
            )
            call.created(mapOf("user" to response))
        }
    }

    route("/user") {
        authenticate {

            // get current user
            getWithAuthenticatedUser(OK) {
                val result = userService.getById()

                UserResponse(
                    email = result.email,
                    username = result.username
                )
            }
        }

        // update user ("[put] /api/user -> [put] /api/profile/{profileId})
        put {
            call.forward("/api/profile")
        }
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