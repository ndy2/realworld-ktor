package ndy.test.util

import ndy.global.context.LoggingContext
import org.slf4j.LoggerFactory

fun loggingContext() = object : LoggingContext {
    override val log = LoggerFactory.getLogger("Test")
}