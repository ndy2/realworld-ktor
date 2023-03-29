package ndy.util

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.resources.*
import io.ktor.server.resources.put
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ndy.context.ApplicationCallContext
import ndy.context.AuthenticatedUserContext
import ndy.domain.user.domain.UserId

suspend inline fun <reified T : Any> ApplicationCall.created(message: T) {
    response.status(HttpStatusCode.Created)
    respond(message)
}

suspend inline fun <reified T : Any> ApplicationCall.ok(message: T) {
    response.status(HttpStatusCode.OK)
    respond(message)
}

fun ApplicationCall.userId(): UserId {
    return this.authentication.principal() ?: authenticationFail("user id not found in token")
}

inline fun <reified T : Any> Route.authenticatedGet(
    vararg configurations: String? = arrayOf(null),
    optional: Boolean = false,
    crossinline build: suspend context(AuthenticatedUserContext, ApplicationCallContext) () -> Unit
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
            build(userContext, callContext)
        }
    }
}

// inline fn 에서 local fn 을 사용할 수 없는 제약때문에 리팩토링 하기 힘듬..
inline fun <reified T : Any> Route.authenticatedPut(
    vararg configurations: String? = arrayOf(null),
    optional: Boolean = false,
    crossinline build: suspend context(AuthenticatedUserContext, ApplicationCallContext) () -> Unit
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
            build(userContext, callContext)
        }
    }
}

