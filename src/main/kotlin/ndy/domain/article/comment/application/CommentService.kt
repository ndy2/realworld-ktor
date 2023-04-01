package ndy.domain.article.comment.application

import ndy.domain.article.comment.domain.Comment
import ndy.domain.article.comment.domain.CommentId
import ndy.domain.article.comment.domain.CommentRepository
import ndy.domain.article.comment.domain.CommentWithAuthor
import ndy.domain.article.domain.ArticleId
import ndy.global.context.AuthenticatedUserContext
import ndy.global.util.mandatoryTransaction

class CommentService(
    private val repository: CommentRepository
) {
    context (AuthenticatedUserContext)
    suspend fun add(articleId: ArticleId, body: String) = mandatoryTransaction {
        val comment = Comment.ofCreate(body)
        repository.save(
            comment = comment,
            authorId = profileId,
            articleId = articleId
        )
    }

    suspend fun getWithAuthorByArticleId(articleId: ArticleId): List<CommentWithAuthor> = mandatoryTransaction {
        repository.findWithAuthorByArticleId(articleId)
    }

    context (AuthenticatedUserContext)
    suspend fun delete(commentId: CommentId, articleId: ArticleId) = mandatoryTransaction {
        // 1. check comment exists & deletable // TODO

        // 2. delete it!
        repository.deleteByCommentId(commentId)
    }
}