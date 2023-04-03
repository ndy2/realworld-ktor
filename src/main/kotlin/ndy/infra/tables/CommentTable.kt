package ndy.infra.tables

import ndy.domain.article.comment.domain.*
import ndy.domain.article.domain.ArticleId
import ndy.global.util.selectWhere
import ndy.infra.tables.ArticleTable.Articles
import ndy.infra.tables.ProfileTable.Profiles
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.kotlin.datetime.CurrentDateTime
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object CommentTable : CommentRepository {

    object Comments : Table() {
        val id = ulong("id").autoIncrement()
        val authorId = ulong("author_id").references(Profiles.id)
        val articleId = ulong("article_id").references(Articles.id)
        val body = varchar("body", 256)
        val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
        val updatedAt = datetime("updated_at")

        override val primaryKey = PrimaryKey(id)
    }

    override fun save(comment: Comment, authorId: AuthorId, articleId: ArticleId): Comment {
        val insertStatement = Comments.insert {
            it[Comments.authorId] = authorId.value
            it[Comments.articleId] = articleId.value
            it[body] = comment.body
            it[createdAt] = comment.createdAt
            it[updatedAt] = comment.updatedAt
        }

        return insertStatement.resultedValues!!.single().toComment()
    }

    override fun findWithAuthorByArticleId(articleId: ArticleId): List<CommentWithAuthor> {
        return (Comments innerJoin Profiles)
            .selectWhere(Comments.articleId eq articleId.value)
            .map { it.toComment() to it.toProfile() }
    }

    override fun deleteByCommentId(commentId: CommentId) {
        Comments.deleteWhere { id eq commentId.value }
    }

    override fun existsByIds(commentId: CommentId, authorId: AuthorId, articleId: ArticleId) = Comments
        .selectWhere(
            Comments.id eq commentId.value,
            Comments.authorId eq commentId.value,
            Comments.articleId eq articleId.value
        )
        .empty().not()
}
