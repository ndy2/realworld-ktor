package ndy.context

import io.ktor.server.application.*

interface ApplicationCallContext {

    val call: ApplicationCall
}