package ndy.infra.tables

import ndy.domain.tag.domain.Tag
import ndy.domain.tag.domain.TagRepository
import org.jetbrains.exposed.sql.*

object TagTable : TagRepository {

    object Tags : Table() {
        val id = ulong("id").autoIncrement()
        val name = varchar("name", 32).uniqueIndex()

        override val primaryKey = PrimaryKey(id)
    }

    override fun save(tag: Tag): Tag {
        val insertStatement = Tags.insert {
            it[name] = tag.name
        }

        return insertStatement.resultedValues?.singleOrNull()?.let(ResultRow::toTag)!!
    }

    override fun findAll() = Tags
        .selectAll()
        .map(ResultRow::toTag)

    override fun findAllWhereNameIn(names: List<String>) = Tags
        .select { Tags.name inList names }
        .map(ResultRow::toTag)
}