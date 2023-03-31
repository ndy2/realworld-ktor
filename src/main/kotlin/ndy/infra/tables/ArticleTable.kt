package ndy.infra.tables

import ndy.domain.article.domain.Article
import ndy.domain.article.domain.ArticleRepository
import ndy.infra.tables.TagTable.Tags
import org.jetbrains.exposed.sql.ReferenceOption.CASCADE
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.CurrentDateTime
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object ArticleTable : ArticleRepository {

    object Articles : Table() {
        val id = ulong("id").autoIncrement()
        val slug = varchar("slug", 128)
        val title = varchar("title", 128)
        val description = varchar("description", 256)
        val body = text("body")
        val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
        val updatedAt = datetime("updated_at")

        override val primaryKey = PrimaryKey(id)
    }

    object ArticleTags : Table() {
        val article = reference("article_id", Articles.id, onDelete = CASCADE)
        val tag = reference("tag_id", Tags.id)
    }

    override fun save(article: Article): Article {
        TODO("Not yet implemented")
    }
}