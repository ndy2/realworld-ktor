package ndy.domain.article.comment.application

import kotlinx.datetime.LocalDateTime
import ndy.domain.article.comment.domain.Comment
import ndy.domain.profile.application.ProfileResult

data class CommentResult(
    val id: ULong,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val body: String,
    val author: AuthorResult
) {
    companion object {
        fun from(comment: Comment, profile: ProfileResult) = CommentResult(
            id = comment.id.value,
            createdAt = comment.createdAt,
            updatedAt = comment.updatedAt,
            body = comment.body,
            author = AuthorResult.from(profile),
        )
    }

    data class AuthorResult(
        val username: String,
        val bio: String?,
        val image: String?,
        val following: Boolean,
    ) {
        companion object {
            fun from(profile: ProfileResult) = AuthorResult(
                username = profile.username,
                bio = profile.bio,
                image = profile.image,
                following = profile.following,
            )
        }
    }
}
