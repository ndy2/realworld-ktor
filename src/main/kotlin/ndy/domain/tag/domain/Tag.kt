package ndy.domain.tag.domain

data class Tag(
        val id: TagId = TagId(0u),
        val name: String
) {
    override fun equals(other: Any?) =
            if (this === other) {
                true
            } else if (javaClass != other?.javaClass) {
                false
            } else {
                name == (other as Tag).name
            }

    override fun hashCode() = name.hashCode()
}

@JvmInline
value class TagId(val value: ULong)
