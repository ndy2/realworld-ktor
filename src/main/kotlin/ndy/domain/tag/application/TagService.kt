package ndy.domain.tag.application

import ndy.domain.tag.domain.Tag
import ndy.domain.tag.domain.TagId
import ndy.domain.tag.domain.TagRepository
import ndy.global.context.AuthenticatedUserContext
import ndy.global.util.Propagation.MANDATORY
import ndy.global.util.transactional

class TagService(
        private val repository: TagRepository
) {
    suspend fun getAll() = transactional {
        // find all tags
        val tags = repository.findAll()

        // return
        tags.map(TagResult::from)
    }

    context (AuthenticatedUserContext)
    suspend fun getOrSaveList(names: List<String>) = transactional(MANDATORY) {
        // 1. get all existed tags
        val existedTags = repository.findAllWhereNameIn(names)

        // 2. iterate names
        names.map { name ->
            // if it's existed get its id else save new tag
            existedTags.find { it.name == name }?.id ?: repository.save(Tag(name = name)).id
        }
    }

    suspend fun getByTagIds(tagIds: List<TagId>, firstTagId: TagId? = null) = transactional(MANDATORY) {
        // find all tags
        val tags = repository.findAllWhereIdIn(tagIds)

        // return
        if (firstTagId == null) {
            tags.map(TagResult::from)
        } else {
            moveFirstTag(tags, firstTagId)
        }
    }

    private fun moveFirstTag(
            tags: List<Tag>,
            firstTagId: TagId?
    ): List<TagResult> {
        val firstTag = tags.first { it.id == firstTagId }
        val result = tags.toMutableList()
        result.remove(firstTag)
        result.add(0, firstTag)
        return result.toList().map(TagResult::from)
    }

    suspend fun getIdByName(tagName: String) = transactional(MANDATORY) {
        repository.findIdByName(tagName)
    }
}
