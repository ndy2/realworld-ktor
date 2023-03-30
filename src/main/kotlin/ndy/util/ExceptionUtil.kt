package ndy.util

import io.konform.validation.ValidationResult
import ndy.exception.AuthenticationException
import ndy.exception.EntityNotFoundException
import ndy.exception.ValidationException

fun authenticationFail(message: String): Nothing = throw AuthenticationException(message)

fun illegalState(): Nothing = throw IllegalStateException()

fun validationFail(message: String? = null, e: Exception? = null): Nothing = throw ValidationException(message, e)
inline fun <reified T> notFound(id: ULong): Nothing =
    throw EntityNotFoundException("${T::class.simpleName} with id :$id not found")

fun checkValidation(condition: Boolean, message: String) {
    if (!condition) {
        validationFail(message)
    }
}

fun <T> ValidationResult<T>.checkAndThrow() {
    if (errors.isNotEmpty()) {
        validationFail(errors.joinToString { it.message })
    }
}