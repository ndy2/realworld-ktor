package ndy.domain.tag.application

import ndy.domain.tag.domain.Tag

data class TagResult(
    val name: String
) {
    companion object {
        fun ofEntity(entity: Tag) = TagResult(
            name = entity.name
        )
    }
}