package ndy.domain.article.comment.domain

import ndy.domain.article.domain.ArticleId
import ndy.domain.profile.domain.Profile
import ndy.domain.profile.domain.ProfileId

typealias AuthorId = ProfileId
typealias Author = Profile
typealias CommentWithAuthor = Pair<Comment, Author>


interface CommentRepository {

    fun save(comment: Comment, authorId: AuthorId, articleId: ArticleId): Comment
    fun findWithAuthorByArticleId(articleId: ArticleId): List<CommentWithAuthor>
    fun deleteByCommentId(commentId: CommentId)
    fun existsByIds(commentId: CommentId, authorId: AuthorId, articleId: ArticleId): Boolean
}