package ndy.util

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*
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
    return this.authentication.principal() ?: fail("user id not found in token")
}

@KtorDsl
fun Route.getWithAuthenticatedUser(body: suspend context(AuthenticatedUserContext) (ApplicationCall) -> Unit) {
    get {

        // (user context, application call) -> unit
        // (user context) -> unit
        // application call 과 관련된 context 는 여기서 전부 소모해 버림
        suspend fun bodyWithModifiedSignature(context: AuthenticatedUserContext) {
            body.invoke(
                context, /* authenticatedUserContext */
                this.context /* application call */
            )
        }
        call.withAuthenticatedUser(::bodyWithModifiedSignature)
    }
}

suspend fun <T> ApplicationCall.withAuthenticatedUser(fn: suspend context(AuthenticatedUserContext) () -> T): T {
    val context = object : AuthenticatedUserContext {
        override val userId = userId()
    }

    return fn(context)
}
