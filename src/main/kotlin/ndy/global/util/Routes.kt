package ndy.global.util

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.authentication
import io.ktor.server.request.header
import io.ktor.server.request.receive
import io.ktor.server.resources.delete
import io.ktor.server.resources.get
import io.ktor.server.resources.post
import io.ktor.server.resources.put
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.util.pipeline.PipelineContext
import ndy.global.context.ApplicationCallContext
import ndy.global.context.AuthenticatedUserContext
import ndy.global.context.applicationCallContext
import ndy.global.context.authenticatedUserContext
import ndy.plugins.TOKEN_SCHEMA

suspend inline fun <reified T : Any> ApplicationCall.created(message: T) {
    response.status(HttpStatusCode.Created)
    respond(message)
}

suspend inline fun <reified T : Any> ApplicationCall.ok(message: T) {
    response.status(HttpStatusCode.OK)
    respond(message)
}

suspend inline fun ApplicationCall.noContent() {
    response.status(HttpStatusCode.NoContent)
    respond(Unit)
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
) = build(authenticatedUserContext(call.authentication), applicationCallContext(call), it)
