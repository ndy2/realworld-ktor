package ndy.api.routers

import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.resources.post
import io.ktor.server.routing.Route
import org.koin.ktor.ext.inject
import ndy.api.dto.LoginRequest
import ndy.api.dto.RegistrationRequest
import ndy.api.dto.UserResponse
import ndy.api.dto.UserUpdateRequest
import ndy.api.resources.User
import ndy.api.resources.Users
import ndy.domain.user.application.UserService
import ndy.global.security.Principal
import ndy.global.util.created
import ndy.global.util.extract
import ndy.global.util.ok
import ndy.global.util.token
import ndy.ktor.context.auth.authenticatedGet
import ndy.ktor.context.auth.authenticatedPut

fun Route.userRouting() {
    val userService by inject<UserService>()

    /**
     * Registration (sign up)
     * POST /api/users
     */
    post<Users> {
        // bind
        val request = call.extract<RegistrationRequest>("user")

        // action
        val result = userService.register(
                username = request.username,
                email = request.email,
                password = request.password
        )

        // return
        val response = UserResponse.ofResult(result)
        call.created(mapOf("user" to response))
    }

    /**
     * Authentication (login)
     * POST /api/users
     */
    post<Users.Login> {
        // bind
        val request = call.extract<LoginRequest>("user")

        // action
        val result = userService.login(
                email = request.email,
                password = request.password
        )

        // return
        val response = UserResponse.ofResult(result)
        call.okUser(response)
    }

    /**
     * Get Current User
     * GET /api/user
     */
    authenticatedGet<User, Principal> {
        // action
        val result = userService.getById()

        // return
        val response = UserResponse.ofResult(result, call.token())
        call.okUser(response)
    }

    /**
     * Update User
     * PUT /api/user
     */
    authenticatedPut<User, Principal> {
        // bind
        val request = call.extract<UserUpdateRequest>("user")

        // action
        val result = userService.update(
                email = request.email,
                password = request.password,
                username = request.username,
                bio = request.bio,
                image = request.image
        )

        // return
        val response = UserResponse.ofResult(result, call.token())
        call.okUser(response)
    }
}

private suspend inline fun ApplicationCall.okUser(response: UserResponse) {
    ok(mapOf("user" to response))
}
