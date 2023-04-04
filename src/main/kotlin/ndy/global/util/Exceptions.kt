package ndy.global.util

import io.konform.validation.ValidationResult
import ndy.global.exception.AccessDeniedException
import ndy.global.exception.AuthenticationException
import ndy.global.exception.EntityNotFoundException
import ndy.global.exception.FieldNotFoundException
import ndy.global.exception.ValidationException
import kotlin.reflect.KProperty1

fun authenticationFail(message: String): Nothing = throw AuthenticationException(message)

fun illegalState(): Nothing = throw IllegalStateException()

fun validationFail(message: String? = null, e: Exception? = null): Nothing = throw ValidationException(message, e)

fun forbiddenIf(condition: Boolean) {
    if (condition) {
        throw AccessDeniedException()
    }
}

inline fun <reified T> notFound(id: ULong): Nothing =
        throw EntityNotFoundException("${T::class.simpleName} with id :$id not found")

inline fun <reified T, F> notFound(field: KProperty1<T, F>, value: F): Nothing =
        throw FieldNotFoundException("${T::class.simpleName} with ${field.name} :$value not found")

fun checkValidation(condition: Boolean, message: String) {
    if (!condition) {
        validationFail(message)
    }
}

/**
 * Check and throw `Konform` validation Result
 *
 * - reference - https://github.com/konform-kt/konform
 * @see validationFail
 * @see io.konform.validation.ValidationResult
 */
fun <T> ValidationResult<T>.checkAndThrow() {
    if (errors.isNotEmpty()) {
        println("this = $this")
        validationFail(errors.joinToString { it.message })
    }
}
