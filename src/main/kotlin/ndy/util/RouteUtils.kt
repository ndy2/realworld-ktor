package ndy.util

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.resources.put
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*
import ndy.context.ApplicationCallContext
import ndy.context.AuthenticatedUserContext
import ndy.context.applicationCallContext
import ndy.context.authenticatedUserContext
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
fun ApplicationCall.userId(): UserId? {
    return this.authentication.principal()
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
) = authenticate(configurations = configurations, optional = optional) {
    get<T> {
        this.run(build, call, it)
    }
}

/* combines `authenticate` & `post<T>` */
inline fun <reified T : Any> Route.authenticatedPost(
    vararg configurations: String? = arrayOf(null),
    optional: Boolean = false,
    crossinline build: suspend context(AuthenticatedUserContext, ApplicationCallContext) (T) -> Unit
) = authenticate(configurations = configurations, optional = optional) {
    post<T> {
        this.run(build, call, it)
    }
}

/* combines `authenticate` & `put<T>` */
inline fun <reified T : Any> Route.authenticatedPut(
    vararg configurations: String? = arrayOf(null),
    optional: Boolean = false,
    crossinline build: suspend context(AuthenticatedUserContext, ApplicationCallContext) (T) -> Unit
) = authenticate(configurations = configurations, optional = optional) {
    put<T> {
        this.run(build, call, it)
    }
}

/* combines `authenticate` & `delete<T>`*/
inline fun <reified T : Any> Route.authenticatedDelete(
    vararg configurations: String? = arrayOf(null),
    optional: Boolean = false,
    crossinline build: suspend context(AuthenticatedUserContext, ApplicationCallContext) (T) -> Unit
) = authenticate(configurations = configurations, optional = optional) {
    delete<T> {
        this.run(build, call, it)
    }
}

suspend inline fun <reified T : Any> PipelineContext<Unit, ApplicationCall>.run(
    crossinline build: suspend context(AuthenticatedUserContext, ApplicationCallContext) (T) -> Unit,
    call: ApplicationCall,
    it: T
) = build(authenticatedUserContext(call), applicationCallContext(call), it)
