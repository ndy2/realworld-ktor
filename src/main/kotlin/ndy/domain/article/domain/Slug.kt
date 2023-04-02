package ndy.domain.article.domain

@JvmInline
value class Slug(
    val value: String
) {
    companion object {
        const val MAX_LENGTH = 256
    }

    init {
        require(value.length < MAX_LENGTH)
    }
}