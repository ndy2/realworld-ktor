package ndy.global.context

import io.ktor.server.application.Application
import io.ktor.server.application.log
import io.ktor.util.logging.Logger

/**
 * context of logger
 *
 * - we can mimic @Slf4j in lombok
 * - we don't need to add
 *    - private val logger = LoggerFactory.getLogger(Foo::class) or
 *    - private val logger = KotlinLogging.logger {}
 * - kind of boilerplate code everytime!
 */
interface LoggingContext {
    val log: Logger
}

fun Application.applicationLoggingContext() = object : LoggingContext {
    override val log = this@applicationLoggingContext.log
}
