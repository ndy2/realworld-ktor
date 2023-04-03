package ndy.test.util

import org.slf4j.LoggerFactory
import ndy.global.context.LoggingContext

fun loggingContext() = object : LoggingContext {
    override val log = LoggerFactory.getLogger("Test")
}
