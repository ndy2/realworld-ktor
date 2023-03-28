package ndy.domain.user.domain

import io.konform.validation.Validation
import io.konform.validation.jsonschema.pattern
import ndy.util.checkAndThrow

data class Email(
    val value: String
) {
    init {
        validateEmail(this).checkAndThrow()
    }
}

val validateEmail = Validation {
    Email::value{ pattern("^([\\w\\.\\_\\-])*[a-zA-Z0-9]+([\\w\\.\\_\\-])*([a-zA-Z0-9])+([\\w\\.\\_\\-])+@([a-zA-Z0-9]+\\.)+[a-zA-Z0-9]{2,8}\$") }
}