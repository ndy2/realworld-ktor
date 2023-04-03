package ndy.domain.article.comment.domain

import ndy.global.util.notUsed
import ndy.global.util.now

import kotlinx.datetime.LocalDateTime

data class Comment(
    val id: CommentId,
    val body: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun ofCreate(body: String) = Comment(
            id = CommentId(0u),
            body = body,
            createdAt = now(),
            updatedAt = notUsed
        )
    }
}

@JvmInline
value class CommentId(val value: ULong)
