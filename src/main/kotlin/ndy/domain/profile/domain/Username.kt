package ndy.domain.profile.domain

import io.konform.validation.Validation
import io.konform.validation.jsonschema.maxLength
import io.konform.validation.jsonschema.minLength
import io.konform.validation.jsonschema.pattern
import ndy.domain.profile.domain.Username.Companion.MAX_LENGTH
import ndy.domain.profile.domain.Username.Companion.MIN_LENGTH
import ndy.domain.profile.domain.Username.Companion.PATTERN
import ndy.global.util.checkAndThrow

data class Username(
        val value: String
) {
    companion object {
        const val MIN_LENGTH = 4
        const val MAX_LENGTH = 64
        const val PATTERN = "^[a-zA-Z0-9_-]*\$" // alphanumeric with `_` and `-`
    }

    init {
        validateUsername(this).checkAndThrow()
    }
}

val validateUsername = Validation {
    Username::value {
        minLength(MIN_LENGTH)
        maxLength(MAX_LENGTH)
        pattern(PATTERN)
    }
}
