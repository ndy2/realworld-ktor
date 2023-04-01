package ndy.plugins

import io.ktor.http.HttpStatusCode.Companion.Forbidden
import io.ktor.http.HttpStatusCode.Companion.InternalServerError
import io.ktor.http.HttpStatusCode.Companion.NotFound
import io.ktor.http.HttpStatusCode.Companion.Unauthorized
import io.ktor.http.HttpStatusCode.Companion.UnprocessableEntity
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import ndy.global.exception.AccessDeniedException
import ndy.global.exception.AuthenticationException
import ndy.global.exception.EntityNotFoundException
import ndy.global.exception.ValidationException

/**
 * Configure Exception Handling with Ktor - StatusPages plugin!
 * *
 * see https://ktor.io/docs/status-pages.html
 */
fun Application.configureExceptionHandling() {

    install(StatusPages) {
        exception<Throwable> { call, cause ->
            val statusCode = when (cause) {
                is AuthenticationException -> Unauthorized //401
                is AccessDeniedException -> Forbidden //403
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