package ndy.api.dto

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import ndy.domain.article.application.ArticleResult
import ndy.domain.article.application.AuthorResult

@Serializable
data class ArticleCreateRequest(
    val title: String,
    val description: String,
    val body: String,
    val tagList: List<String>,
)

@Serializable
data class ArticleUpdateRequest(
    val title: String?,
    val description: String?,
    val body: String?,
)

@Serializable
data class ArticleResponse(
    val slug: String,
    val title: String,
    val description: String,
    val body: String,
    val tagList: List<String>,
    val createdAt: String,
    val updatedAt: String,
    val favorited: Boolean,
    val favoritesCount: Int,
    val author: AuthorResponse,
) {
    companion object {
        fun ofResult(result: ArticleResult) = ArticleResponse(
            slug = result.slug,
            title = result.title,
            description = result.description,
            body = result.body,
            tagList = result.tagList,
            createdAt = "${result.createdAt}Z", // It is hard to customize serializer for kotlinx.datetime.LocalDateTime ...
            updatedAt = "${result.updatedAt}Z",
            favorited = result.favorited,
            favoritesCount = result.favoritesCount,
            author = AuthorResponse.ofResult(result.author),
        )
    }

    @Serializable
    data class AuthorResponse(
        val username: String,
        val bio: String?,
        val image: String?,
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

@Serializable
data class ArticleResponseList(
    val articles: List<ArticleResponse>,
    val articlesCount: Int,
)