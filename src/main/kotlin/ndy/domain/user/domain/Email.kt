package ndy.domain.user.domain

import io.konform.validation.Validation
import io.konform.validation.jsonschema.maxLength
import io.konform.validation.jsonschema.minLength
import io.konform.validation.jsonschema.pattern
import ndy.global.util.checkAndThrow

data class Email(
    val value: String
) {
    companion object {
        const val MAX_LENGTH = 128
    }

    init {
        validateEmail(this).checkAndThrow()
    }
}

private val validateEmail = Validation {
    Email::value {
        minLength(8)
        maxLength(Email.MAX_LENGTH)
        pattern(
            "^([\\w\\.\\_\\-])*[a-zA-Z0-9]+([\\w\\.\\_\\-])*([a-zA-Z0-9])+([\\w\\.\\_\\-])+@([a-zA-Z0-9]+\\.)+[a-zA-Z0-9]{2,8}\$"
        )
    }
}
