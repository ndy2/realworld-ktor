package ndy.domain.article.comment.application

import ndy.domain.article.comment.domain.CommentRepository

class CommentService(
    private val repository: CommentRepository
) {
}