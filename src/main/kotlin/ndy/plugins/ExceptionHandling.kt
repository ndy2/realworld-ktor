package ndy.plugins

import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.InternalServerError
import io.ktor.http.HttpStatusCode.Companion.NotFound
import io.ktor.http.HttpStatusCode.Companion.Unauthorized
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import ndy.exception.AuthenticationException
import ndy.exception.RealworldRuntimeException

fun Application.configureExceptionHandling() {

    install(StatusPages) {
        exception<Throwable> { call, cause ->
            val statusCode = when (cause) {
                is AuthenticationException -> Unauthorized //401
                is NotFoundException -> NotFound // 404
                is RealworldRuntimeException -> BadRequest // 400
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