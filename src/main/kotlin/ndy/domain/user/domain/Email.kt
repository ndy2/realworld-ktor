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

        private object Validate : Validation<Email> {
            override fun validate(value: Email) = Validation {
                Email::value{
                    minLength(8)
                    maxLength(MAX_LENGTH)
                    pattern("^([\\w\\.\\_\\-])*[a-zA-Z0-9]+([\\w\\.\\_\\-])*([a-zA-Z0-9])+([\\w\\.\\_\\-])+@([a-zA-Z0-9]+\\.)+[a-zA-Z0-9]{2,8}\$")
                }
            }.invoke(value)
        }
    }

    init {
        Validate.validate(this)
    }
}

