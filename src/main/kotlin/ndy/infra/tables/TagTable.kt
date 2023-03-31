package ndy.infra.tables

import ndy.domain.tag.domain.Tag
import ndy.domain.tag.domain.TagRepository
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.selectAll

object TagTable : TagRepository {

    object Tags : Table() {
        val id = ulong("id").autoIncrement()
        val name = varchar("name", 32).uniqueIndex()

        override val primaryKey = PrimaryKey(id)
    }

    override fun findAll() = Tags
        .selectAll()
        .map(ResultRow::toTag)

    override fun save(tag: Tag): Tag {
        TODO("Not yet implemented")
    }

    override fun findAllWhereNameIn(names: List<String>): List<Tag> {
        TODO("Not yet implemented")
    }
}