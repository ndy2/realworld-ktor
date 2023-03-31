package ndy.api.dto

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import ndy.domain.article.comment.CommentResult
import ndy.domain.article.comment.CommentResult.AuthorResult


@Serializable
data class CommentAddRequest(
    val body: String
)

@Serializable
data class CommentResponse(
    val id: ULong,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val body: String,
    val author: AuthorResponse
) {
    companion object {
        fun ofResult(result: CommentResult) = CommentResponse(
            id = result.id,
            createdAt = LocalDateTime.parse(result.createdAt.toString()),
            updatedAt = LocalDateTime.parse(result.createdAt.toString()),
            body = result.body,
            author = AuthorResponse.ofResult(result.author)
        )
    }

    @Serializable
    data class AuthorResponse(
        val username: String,
        val bio: String,
        val image: String,
        val following: Boolean,
    ) {
        companion object {
            fun ofResult(result: AuthorResult) = AuthorResponse(
                username = result.username,
                bio = result.bio,
                image = result.image,
                following = result.following,
            )
        }
    }
}