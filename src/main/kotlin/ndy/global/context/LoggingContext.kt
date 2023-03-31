package ndy.global.context

import io.ktor.util.logging.*
import org.slf4j.LoggerFactory

interface LoggingContext {
    val log: Logger
}

//TODO - use application.environment.log
object DefaultLoggingContext : LoggingContext {
    override val log: Logger = LoggerFactory.getLogger("Application")
}