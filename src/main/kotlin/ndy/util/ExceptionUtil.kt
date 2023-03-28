package ndy.util

import io.konform.validation.ValidationResult
import ndy.exception.RealworldRuntimeException

fun fail(
    message: String? = null,
    e: Exception? = null
): Nothing {
    throw RealworldRuntimeException(message, e)
}


fun <T> ValidationResult<T>.checkAndThrow() {
    if (errors.isNotEmpty()) {
        throw RealworldRuntimeException(errors.joinToString { it.message })
    }
}