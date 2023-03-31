package ndy.infra.tables

import ndy.domain.tag.domain.Tag
import ndy.domain.tag.domain.TagRepository
import org.jetbrains.exposed.sql.Table

object TagTable : TagRepository {

    object Tags : Table() {
        val id = ulong("id").autoIncrement()
        val name = varchar("name", 32).uniqueIndex()

        override val primaryKey = PrimaryKey(id)
    }

    override fun save(tag: Tag): Tag {
        TODO("Not yet implemented")
    }

    override fun findAllWhereNameIn(names: List<String>): List<Tag> {
        TODO("Not yet implemented")
    }


}