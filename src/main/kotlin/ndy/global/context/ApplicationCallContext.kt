package ndy.global.context

import io.ktor.server.application.ApplicationCall

/**
 * context of ApplicationCall
 * - used by RouteUtils.authenticatedXXX
 *
 * background
 * - function parameter with `context receiver` and `receiver type` is not allowed
 * - that is we cannot pass below two context for authenticatedXXX methods
 *    - `context receiver` - AuthenticatedUserContext with
 *    - `receiver type` - PipelineContext<Unit, ApplicationCall>
 *
 * - but we can pass multiple `context receiver`
 * - thus I introduced this context and used it in authenticatedXXX as in below function type
 * - context(AuthenticatedUserContext, ApplicationCallContext) (T) -> Unit
 */
interface ApplicationCallContext {
    val call: ApplicationCall
}

fun applicationCallContext(call: ApplicationCall) =
        object : ApplicationCallContext {
            override val call = call
        }
