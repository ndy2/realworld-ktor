package ndy.domain.tag.application

import ndy.domain.tag.domain.Tag

data class TagResult(
    val name: String
) {
    companion object {
        fun from(entity: Tag) = TagResult(
            name = entity.name
        )
    }
}
