package ndy.domain.tag.application

import ndy.domain.tag.domain.Tag
import ndy.domain.tag.domain.TagId
import ndy.domain.tag.domain.TagRepository
import ndy.global.context.AuthenticatedUserContext
import ndy.global.util.mandatoryTransaction
import ndy.global.util.requiresNewTransaction

class TagService(
    private val repository: TagRepository
) {
    suspend fun getAll() = requiresNewTransaction {
        // find all tags
        val tags = repository.findAll()

        // return
        tags.map(TagResult::ofEntity)
    }

    context (AuthenticatedUserContext)
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

    suspend fun getByTagIds(tagIds: List<TagId>) = requiresNewTransaction {
        // find all tags
        val tags = repository.findAllWhereIdIn(tagIds)

        // return
        tags.map(TagResult::ofEntity)
    }

    fun getIdByName(tagName: String): TagId? {
        return repository.findByName(tagName)
    }
}