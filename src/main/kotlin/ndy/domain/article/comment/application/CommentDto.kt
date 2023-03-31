package ndy.domain.article.comment.application

import java.time.LocalDateTime

data class CommentResult(
    val id: ULong,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val body: String,
    val author: AuthorResult
) {
    data class AuthorResult(
        val username: String,
        val bio: String,
        val image: String,
        val following: Boolean,
    )
}
