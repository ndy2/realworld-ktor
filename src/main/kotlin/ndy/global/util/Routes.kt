package ndy.global.util

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.header
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import ndy.global.security.Principal
import ndy.ktor.context.auth.AuthenticationContext
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

context (AuthenticationContext<Principal>)
fun ApplicationCall.token(): String {
    return request.header("AUTHORIZATION")?.substringAfter("$TOKEN_SCHEMA ") ?: illegalState()
}

suspend inline fun <reified T : Any> ApplicationCall.extract(key: String) = receive<Map<String, T>>()[key]!!
