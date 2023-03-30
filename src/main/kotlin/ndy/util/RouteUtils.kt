package ndy.util

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.put
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ndy.context.ApplicationCallContext
import ndy.context.AuthenticatedUserContext
import ndy.domain.user.domain.UserId
import ndy.plugins.TOKEN_SCHEMA

suspend inline fun <reified T : Any> ApplicationCall.created(message: T) {
    response.status(HttpStatusCode.Created)
    respond(message)
}

suspend inline fun <reified T : Any> ApplicationCall.ok(message: T) {
    response.status(HttpStatusCode.OK)
    respond(message)
}

// principal is created @configureSecurity -> jwt.validate
fun ApplicationCall.userId(): UserId {
    return this.authentication.principal() ?: authenticationFail("user id not found in token")
}

context (AuthenticatedUserContext)
fun ApplicationCall.token(): String {
    return request.header("AUTHORIZATION")?.substringAfter("$TOKEN_SCHEMA ") ?: illegalState()
}

suspend inline fun <reified T : Any> ApplicationCall.extract(key: String) = receive<Map<String, T>>()[key]!!

/**
 * custom dsl which combines `authenticate` & `get<T>`
 * also add `context(AuthenticatedUserContext, ApplicationCallContext)` for flexibility
 */
inline fun <reified T : Any> Route.authenticatedGet(
    vararg configurations: String? = arrayOf(null),
    optional: Boolean = false,
    crossinline build: suspend context(AuthenticatedUserContext, ApplicationCallContext) (T) -> Unit
): Route {
    return authenticate(
        configurations = configurations,
        optional = optional
    ) {
        get<T> {
            val userContext = object : AuthenticatedUserContext {
                override val userId = call.userId()
            }
            val callContext = object : ApplicationCallContext {
                override val call = this@get.call
            }
            build(userContext, callContext, it)
        }
    }
}

/**
 * custom dsl which combines `authenticate` & `put<T>`
 * also add `context(AuthenticatedUserContext, ApplicationCallContext)` for flexibility
 * *
 * since inline fn cannot use local fn, it is hard two refactor duplication in authenticatedGet & authenticatedPut
 */
inline fun <reified T : Any> Route.authenticatedPut(
    vararg configurations: String? = arrayOf(null),
    optional: Boolean = false,
    crossinline build: suspend context(AuthenticatedUserContext, ApplicationCallContext) (T) -> Unit
): Route {
    return authenticate(
        configurations = configurations,
        optional = optional
    ) {
        put<T> {
            val userContext = object : AuthenticatedUserContext {
                override val userId = call.userId()
            }
            val callContext = object : ApplicationCallContext {
                override val call = this@put.call
            }
            build(userContext, callContext, it)
        }
    }
}

