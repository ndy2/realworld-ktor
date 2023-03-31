package ndy.domain.tag.domain

interface TagRepository {

    fun findAll(): List<Tag>
    fun save(tag: Tag): Tag
    fun findAllWhereNameIn(names: List<String>): List<Tag>
}