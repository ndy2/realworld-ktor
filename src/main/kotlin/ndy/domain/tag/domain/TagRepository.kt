package ndy.domain.tag.domain

interface TagRepository {

    fun save(tag: Tag): Tag
    fun findAll(): List<Tag>
    fun findAllWhereNameIn(names: List<String>): List<Tag>
}