package ndy.domain.article.comment.application

import kotlinx.datetime.LocalDateTime
import ndy.domain.article.comment.domain.Comment
import ndy.domain.profile.application.ProfileResult
import ndy.domain.profile.domain.Profile

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

        fun from(comment: Comment, profile: Profile, following: Boolean) = CommentResult(
            id = comment.id.value,
            createdAt = comment.createdAt,
            updatedAt = comment.updatedAt,
            body = comment.body,
            author = AuthorResult.from(profile, following),
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

            fun from(profile: Profile, following: Boolean) = AuthorResult(
                username = profile.username.value,
                bio = profile.bio?.value,
                image = profile.image?.fullPath,
                following = following,
            )
        }
    }
}
