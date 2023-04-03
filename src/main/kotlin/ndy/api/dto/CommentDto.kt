package ndy.api.dto

import kotlinx.serialization.Serializable
import ndy.domain.article.comment.application.CommentResult
import ndy.domain.article.comment.application.CommentResult.AuthorResult

@Serializable
data class CommentAddRequest(
    val body: String
)

@Serializable
data class CommentResponse(
    val id: ULong,
    val createdAt: String,
    val updatedAt: String,
    val body: String,
    val author: AuthorResponse
) {
    companion object {
        fun ofResult(result: CommentResult) = CommentResponse(
            id = result.id,
            createdAt = "${result.createdAt}Z",
            updatedAt = "${result.updatedAt}Z",
            // It is hard to customize serializer for kotlinx.datetime.LocalDateTime ...
            body = result.body,
            author = AuthorResponse.ofResult(result.author)
        )
    }

    @Serializable
    data class AuthorResponse(
        val username: String,
        val bio: String?,
        val image: String?,
        val following: Boolean
    ) {
        companion object {
            fun ofResult(result: AuthorResult) = AuthorResponse(
                username = result.username,
                bio = result.bio,
                image = result.image,
                following = result.following
            )
        }
    }
}
