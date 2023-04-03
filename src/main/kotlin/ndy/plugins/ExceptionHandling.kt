package ndy.plugins

import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.Forbidden
import io.ktor.http.HttpStatusCode.Companion.InternalServerError
import io.ktor.http.HttpStatusCode.Companion.NotFound
import io.ktor.http.HttpStatusCode.Companion.Unauthorized
import io.ktor.http.HttpStatusCode.Companion.UnprocessableEntity
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.NotFoundException
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import ndy.global.exception.AuthenticationException
import ndy.global.exception.EntityNotFoundException
import ndy.global.exception.FieldNotFoundException
import ndy.global.exception.ValidationException

/**
 * Configure Exception Handling with Ktor - StatusPages plugin!
 *
 * reference - https://ktor.io/docs/status-pages.html
 */
fun Application.configureExceptionHandling() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            val statusCode = when (cause) {
                is FieldNotFoundException -> BadRequest // 400 - field not found
                is EntityNotFoundException -> BadRequest // 400 - entity not found
                is AuthenticationException -> Unauthorized // 401
                is AccessDeniedException -> Forbidden // 403
                is NotFoundException -> NotFound // 404 - resource not found
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
