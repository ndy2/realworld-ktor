package ndy.domain.article.comment.application

import ndy.domain.article.comment.domain.Comment
import ndy.domain.article.comment.domain.CommentId
import ndy.domain.article.comment.domain.CommentRepository
import ndy.domain.article.comment.domain.CommentWithAuthor
import ndy.domain.article.domain.ArticleId
import ndy.global.security.AuthenticationContext
import ndy.global.util.Propagation.MANDATORY
import ndy.global.util.forbiddenIf
import ndy.global.util.transactional

class CommentService(
        private val repository: CommentRepository
) {
    context (AuthenticationContext)
    suspend fun add(articleId: ArticleId, body: String) = transactional(MANDATORY) {
        val comment = Comment.ofCreate(body)
        repository.save(
                comment = comment,
                authorId = principal.profileId,
                articleId = articleId
        )
    }

    suspend fun getWithAuthorByArticleId(articleId: ArticleId): List<CommentWithAuthor> = transactional(MANDATORY) {
        repository.findWithAuthorByArticleId(articleId)
    }

    context (AuthenticationContext)
    suspend fun delete(commentId: CommentId, articleId: ArticleId) = transactional(MANDATORY) {
        // 1. check comment exists & deletable
        forbiddenIf(!repository.existsByIds(commentId, principal.profileId, articleId))

        // 2. delete it!
        repository.deleteByCommentId(commentId)
    }
}
