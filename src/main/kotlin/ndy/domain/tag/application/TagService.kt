package ndy.domain.tag.application

import ndy.domain.tag.domain.Tag
import ndy.domain.tag.domain.TagRepository
import ndy.global.util.mandatoryTransaction

class TagService(
    private val repository: TagRepository
) {
    fun getAll() {

    }

    suspend fun getOrSaveList(names: List<String>) = mandatoryTransaction {
        // 1. get all existed tags
        val existedTags = repository.findAllWhereNameIn(names)

        // 2. iterate names
        names.map { name ->
            // if it's existed get its id
            existedTags.find { it.name == name }?.id
            // else save new tag
                ?: repository.save(Tag(name = name)).id
        }
    }
}