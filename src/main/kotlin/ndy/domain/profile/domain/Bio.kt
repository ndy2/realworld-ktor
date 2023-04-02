package ndy.domain.profile.domain

import io.konform.validation.Validation
import io.konform.validation.jsonschema.maxLength
import ndy.domain.profile.domain.Bio.Companion.MAX_LENGTH
import ndy.global.util.checkAndThrow

data class Bio(
    val value: String
){
    companion object{
        const val MAX_LENGTH = 512
    }

    init {
        validateBio(this).checkAndThrow()
    }
}

val validateBio = Validation {
    Bio::value { maxLength(MAX_LENGTH) }
}
