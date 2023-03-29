package ndy.plugins

import io.ktor.http.HttpStatusCode.Companion.InternalServerError
import io.ktor.http.HttpStatusCode.Companion.NotFound
import io.ktor.http.HttpStatusCode.Companion.Unauthorized
import io.ktor.http.HttpStatusCode.Companion.UnprocessableEntity
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import ndy.exception.AuthenticationException
import ndy.exception.EntityNotFoundException
import ndy.exception.ValidationException

fun Application.configureExceptionHandling() {

    install(StatusPages) {
        exception<Throwable> { call, cause ->
            val statusCode = when (cause) {
                is AuthenticationException -> Unauthorized //401
                is NotFoundException -> NotFound // 404 - resource not found
                is EntityNotFoundException -> NotFound // 404 - entity not found
                is ValidationException -> UnprocessableEntity // 422 - validation failed
                else -> InternalServerError // 500
            }

            call.respond(
                statusCode,
                errorResponse(cause.message ?: cause.toString())
            )
        }
    }
}

fun errorResponse(message: String) = mapOf("errors" to mapOf("body" to message))