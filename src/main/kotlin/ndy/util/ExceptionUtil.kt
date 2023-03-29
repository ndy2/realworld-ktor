package ndy.util

import io.konform.validation.ValidationResult
import ndy.exception.RealworldRuntimeException

fun fail(
    message: String? = null,
    e: Exception? = null
): Nothing {
    throw RealworldRuntimeException(message, e)
}

fun checkCondition(
    condition: Boolean,
    message: String = "condition not matched"
) {
    if (!condition) {
        fail(message)
    }
}

fun <T> ValidationResult<T>.checkAndThrow() {
    if (errors.isNotEmpty()) {
        fail(errors.joinToString { it.message })
    }
}