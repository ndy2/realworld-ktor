package ndy.api.routers

import io.ktor.server.application.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import ndy.api.dto.LoginRequest
import ndy.api.dto.RegistrationRequest
import ndy.api.dto.UserResponse
import ndy.api.dto.UserUpdateRequest
import ndy.api.resources.User
import ndy.api.resources.Users
import ndy.domain.user.application.UserService
import ndy.global.util.*
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
