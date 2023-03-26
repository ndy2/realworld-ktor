package ndy.routers

import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import ndy.domain.user.application.UserService
import ndy.util.created
import org.koin.ktor.ext.inject

fun Route.userRouting() {

    val userService by inject<UserService>()

    route("/users") {

        // authentication
        post("/login") {
            call.respond("login request received!")
        }

        // registration
        post {
            // binding
            val request = call.receive<Map<String, RegistrationRequest>>()["user"]
                ?: return@post call.respond(BadRequest, "no user provided")

            // invoke service
            val result = userService.register(
                username = request.username,
                email = request.email,
                password = request.password,
            )

            // response
            val response = UserResponse(email = result.email, username = result.username)
            call.created(response)
        }
    }
}

@Serializable
data class LoginRequest(val email: String, val password: String)

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