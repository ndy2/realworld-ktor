package ndy.domain.profile.domain

data class Image(
    val storePath: String,
    val fileName: String,
    val extension: String,
) {
    val fullPath: String by lazy { "$storePath/$fileName.$extension" }

    companion object {
        fun ofFullPath(fullPath: String): Image {
            val i1 = fullPath.lastIndexOf('/')
            val i2 = fullPath.lastIndexOf(".")

            return Image(
                storePath = fullPath.substring(0 until i1),
                fileName = fullPath.substring(i1 + 1 until i2),
                extension = fullPath.substring(i2 + 1),
            )
        }
    }
}