package ndy.global.context

import io.ktor.server.application.*
import io.ktor.util.logging.*

interface LoggingContext {
    val log: Logger
}

fun Application.applicationLoggingContext() = object : LoggingContext {
    override val log = this@applicationLoggingContext.log
}
