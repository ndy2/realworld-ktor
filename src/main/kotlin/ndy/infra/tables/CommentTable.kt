package ndy.infra.tables

import ndy.domain.article.comment.domain.AuthorId
import ndy.domain.article.comment.domain.Comment
import ndy.domain.article.comment.domain.CommentRepository
import ndy.domain.article.domain.ArticleId
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.kotlin.datetime.CurrentDateTime
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object CommentTable : CommentRepository {

    object Comments : Table() {
        val id = ulong("id")
        val authorId = ulong("author_id")/*.references(Profiles.id)*/
        val body = varchar("body", 256)
        val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
        val updatedAt = datetime("updated_at")

        override val primaryKey = PrimaryKey(ArticleTable.Articles.id)
    }

    override fun save(comment: Comment, authorId: AuthorId, articleId: ArticleId): Comment {
        val insertStatement = Comments.insert {
            it[Comments.authorId] = authorId.value
            it[body] = comment.body
            it[createdAt] = comment.createdAt
            it[updatedAt] = comment.updatedAt
        }

        return insertStatement.resultedValues!!.single().toComment()
    }
}