package ndy.domain.profile.domain

import io.konform.validation.Validation
import io.konform.validation.jsonschema.enum
import io.konform.validation.jsonschema.maxLength
import io.konform.validation.jsonschema.minLength
import ndy.domain.profile.domain.Image.Companion.ALLOWED_EXTENSIONS
import ndy.domain.profile.domain.Image.Companion.FILE_NAME_MAX_LENGTH
import ndy.domain.profile.domain.Image.Companion.FILE_NAME_MIN_LENGTH
import ndy.domain.profile.domain.Image.Companion.MAX_LENGTH
import ndy.global.exception.ValidationException
import ndy.global.util.checkAndThrow

class Image private constructor(
    val storePath: String,
    val fileName: String,
    val extension: String,
) {
    val fullPath = "$storePath/$fileName.$extension"

    companion object {
        const val FILE_NAME_MIN_LENGTH = 5
        const val FILE_NAME_MAX_LENGTH = 10
        const val MAX_LENGTH = 128
        val ALLOWED_EXTENSIONS = listOf("jpeg", "jpg", "png")

        fun ofFullPath(fullPath: String): Image {
            val i1 = fullPath.lastIndexOf('/')
            val i2 = fullPath.lastIndexOf(".")

            if (i1 == -1 || i2 == -1 || i1 >= i2) throw ValidationException("invalid image path")

            return Image(
                storePath = fullPath.substring(0 until i1),
                fileName = fullPath.substring(i1 + 1 until i2),
                extension = fullPath.substring(i2 + 1),
            )
        }
    }

    init {
        validateImage(this).checkAndThrow()
    }

    override fun toString(): String {
        return "Image(fullPath='$fullPath')"
    }
}

val validateImage = Validation {
    Image::fileName { minLength(FILE_NAME_MIN_LENGTH); maxLength(FILE_NAME_MAX_LENGTH) }
    Image::extension { enum(*ALLOWED_EXTENSIONS.toTypedArray()) }
    Image::fullPath { maxLength(MAX_LENGTH) }
}