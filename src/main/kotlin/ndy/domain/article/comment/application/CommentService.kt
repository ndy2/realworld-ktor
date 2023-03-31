package ndy.domain.article.comment.application

import ndy.domain.article.comment.domain.Comment
import ndy.domain.article.comment.domain.CommentRepository
import ndy.domain.article.domain.ArticleId
import ndy.global.context.ProfileIdContext
import ndy.global.util.mandatoryTransaction

class CommentService(
    private val repository: CommentRepository
) {
    context (ProfileIdContext)
    suspend fun add(articleId: ArticleId, body: String) = mandatoryTransaction {
        val comment = Comment.ofCreate(body)
        repository.save(
            comment = comment,
            authorId = profileId,
            articleId = articleId
        )
    }
}