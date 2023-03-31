package ndy.infra.tables

import ndy.domain.article.domain.Article
import ndy.domain.article.domain.ArticleRepository
import ndy.domain.article.domain.ArticleWithAuthor
import ndy.domain.profile.domain.ProfileId
import ndy.infra.tables.TagTable.Tags
import org.jetbrains.exposed.sql.ReferenceOption.CASCADE
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
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

    override fun save(article: Article, authorId: ProfileId): Article {
        // insert article
        val articleInsertStatement = Articles.insert {
            it[slug] = article.slug
            it[title] = article.title
            it[description] = article.description
            it[body] = article.body
            it[createdAt] = article.createdAt
            it[updatedAt] = article.updatedAt
        }
        val articleResultRow = articleInsertStatement.resultedValues!!.single()
        val articleId = articleResultRow[Articles.id]

        // insert article tags
        article.tagIds.forEach { tagId -> insertArticleTags(articleId, tagId.value) }

        // return
        return resultRowToArticle(articleResultRow, article.tagIds)
    }

    override fun findBySlugWithAuthor(slug: String): ArticleWithAuthor? {
        TODO("Not yet implemented")
    }

    override fun updateBySlug(
        slug: String,
        updatedSlug: String,
        title: String?,
        description: String?,
        body: String?
    ): Article? {
        TODO("Not yet implemented")
    }

    override fun findBySlug(slug: String): Article? {
        TODO("Not yet implemented")
    }

    override fun deleteBySlug(slug: String) {
        TODO("Not yet implemented")
    }

    private fun insertArticleTags(articleId: ULong, tagId: ULong) {
        ArticleTags.insert {
            it[article] = articleId
            it[tag] = tagId
        }
    }
}