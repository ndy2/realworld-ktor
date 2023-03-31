package ndy.infra.tables

import ndy.domain.article.domain.*
import ndy.domain.tag.domain.TagId
import ndy.global.util.now
import ndy.infra.tables.ProfileTable.Profiles
import ndy.infra.tables.TagTable.Tags
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.ReferenceOption.CASCADE
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.kotlin.datetime.CurrentDateTime
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object ArticleTable : ArticleRepository {

    object Articles : Table() {
        val id = ulong("id").autoIncrement()
        val authorId = ulong("author_id").references(Profiles.id)
        val slug = varchar("slug", 128)
        val title = varchar("title", 128)
        val description = varchar("description", 256)
        val body = text("body")
        val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
        val updatedAt = datetime("updated_at")

        override val primaryKey = PrimaryKey(id)
    }

    object ArticleTags : Table("ARTICLE_TAG") {
        val article = reference("article_id", Articles.id, onDelete = CASCADE)
        val tag = reference("tag_id", Tags.id)
    }

    override fun save(article: Article, authorId: AuthorId, tagIds: List<TagId>): Article {
        // insert article
        val articleInsertStatement = Articles.insert {
            it[Articles.authorId] = authorId.value
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
        tagIds.forEach { tagId ->
            ArticleTags.insert {
                it[this.article] = articleId
                it[tag] = tagId.value
            }
        }

        // return
        return articleResultRow.toArticle()
    }

    override fun findWithAuthorAndTagIdsBySlug(slug: String): ArticleWithAuthorAndTagIds? {
        val articleWithAuthor = (Articles innerJoin Profiles)
            .select { Articles.slug eq slug }
            .map { it.toArticle() to it.toProfile() }
            .singleOrNull() ?: return null
        val articleId = articleWithAuthor.first.id

        val tagIds = ArticleTags
            .select { ArticleTags.article eq articleId.value }
            .map { TagId(it[ArticleTags.tag]) }
        return ArticleWithAuthorAndTagIds(articleWithAuthor.first, articleWithAuthor.second, tagIds)
    }

    override fun updateBySlug(
        slug: String,
        updatedSlug: String,
        title: String?,
        description: String?,
        body: String?
    ): ArticleWithAuthorId? {
        Articles.update({ Articles.slug eq slug }) {
            it[Articles.slug] = updatedSlug
            if (title != null) it[Articles.title] = title
            if (description != null) it[Articles.description] = description
            if (body != null) it[Articles.body] = body
            it[Articles.updatedAt] = now()
        }

        return findBySlug(slug)
    }

    override fun findBySlug(slug: String) = Articles
        .select { Articles.slug eq slug }
        .map { it.toArticle() to AuthorId(it[Articles.authorId]) }
        .singleOrNull()

    override fun deleteBySlug(slug: String) = Articles
        .deleteWhere { Articles.slug eq slug }

}