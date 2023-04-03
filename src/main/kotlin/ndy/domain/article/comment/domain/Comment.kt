package ndy.domain.article.comment.domain

import kotlinx.datetime.LocalDateTime
import ndy.global.util.notUsed
import ndy.global.util.now

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
