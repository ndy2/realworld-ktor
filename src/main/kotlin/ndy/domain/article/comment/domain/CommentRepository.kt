package ndy.domain.article.comment.domain

import ndy.domain.article.domain.ArticleId
import ndy.domain.profile.domain.ProfileId

typealias AuthorId = ProfileId

interface CommentRepository {
    fun save(comment: Comment, authorId: AuthorId, articleId: ArticleId): Comment
}