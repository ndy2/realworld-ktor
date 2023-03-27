package ndy.util

import ndy.exception.RealworldRuntimeException

fun fail(
    message: String? = null,
    e: Exception? = null
): Nothing {
    throw RealworldRuntimeException(message, e)
}