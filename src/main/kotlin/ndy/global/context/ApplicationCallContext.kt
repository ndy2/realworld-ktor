package ndy.global.context

import io.ktor.server.application.*

interface ApplicationCallContext {
    val call: ApplicationCall
}

fun applicationCallContext(call: ApplicationCall) =
    object : ApplicationCallContext {
        override val call = call
    }
