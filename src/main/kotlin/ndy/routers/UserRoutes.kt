package ndy.routers

import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import ndy.domain.user.application.UserService
import ndy.domain.user.domain.UserId
import ndy.util.created
import ndy.util.ok
import org.koin.ktor.ext.inject

fun Route.userRouting() {
    val userService by inject<UserService>()

    route("/users") {
        // authentication
        post("/login") {
            // binding
            val request = call.receive<Map<String, LoginRequest>>()["user"]
                ?: return@post call.respond(BadRequest, "no use provided")

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
            val request = call.receive<Map<String, RegistrationRequest>>()["user"]
                ?: return@post call.respond(BadRequest, "no user provided")

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
            get {
                val userId = call.authentication.principal<UserId>()!!

                val result = userService.getById(userId)

                val response = UserResponse(
                    email = result.email,
                    username = result.username
                )
                call.ok(response)
            }

            // update user
            put {

            }
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