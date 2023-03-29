package ndy.util

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*
import io.ktor.util.pipeline.*
import io.ktor.util.reflect.*
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

@KtorDsl
inline fun <reified T> Route.getWithAuthenticatedUser(
    status: HttpStatusCode,
    noinline body: suspend context(AuthenticatedUserContext) (ApplicationCall) -> T
) {
    get {
        val response = call.withAuthenticatedUser(body, this.context)
        call.respond(status, response, typeInfo<T>())
    }
}

@KtorDsl
inline fun <reified T> Route.putWithAuthenticatedUser(
    status: HttpStatusCode,
    noinline body: suspend context(AuthenticatedUserContext) (ApplicationCall) -> T
) {
    put {
        val response = call.withAuthenticatedUser(body, this.context)
        call.respond(status, response, typeInfo<T>())
    }
}

// code referenced @ https://youtu.be/NxDIq-rFXUM
suspend fun <T> ApplicationCall.withAuthenticatedUser(
    fn: suspend context(AuthenticatedUserContext) (ApplicationCall) -> T,
    ac: ApplicationCall
): T {
    val userContext = object : AuthenticatedUserContext {
        override val userId = userId()
    }

    return fn(userContext, ac)
}

// code from https://stackoverflow.com/questions/60443412/how-to-redirect-internally-in-ktor
suspend fun ApplicationCall.forward(path: String) {
    val cp = object : RequestConnectionPoint by this.request.local {
        override val uri: String = path
    }
    val req = object : ApplicationRequest by this.request {
        override val local: RequestConnectionPoint = cp
    }
    val call = object : ApplicationCall by this {
        override val request: ApplicationRequest = req
    }

    this.application.execute(call)
}