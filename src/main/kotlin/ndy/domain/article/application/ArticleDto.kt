package ndy.domain.article.application

import kotlinx.datetime.LocalDateTime
import ndy.domain.article.domain.Article
import ndy.domain.article.domain.Author
import ndy.domain.profile.application.ProfileResult
import ndy.domain.tag.application.TagResult

data class ArticleSearchCond(
    val tag: String?,
    val author: String?,
    val favorited: String?,
    val limit: Int,
    val offset: Int,
)

data class ArticleResult(
    val slug: String,
    val title: String,
    val description: String,
    val body: String,
    val tagList: List<String>,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val favorited: Boolean,
    val favoritesCount: Int,
    val author: AuthorResult,
) {
    companion object {
        fun from(
            article: Article,
            tagResults: List<TagResult>,
            favorited: Boolean,
            favoritesCount: Int,
            author: Author,
            following: Boolean,
        ) = ArticleResult(
            slug = article.slug,
            title = article.title,
            description = article.description,
            body = article.body,
            tagList = tagResults.map(TagResult::name),
            createdAt = article.createdAt,
            updatedAt = article.updatedAt,
            favorited = favorited,
            favoritesCount = favoritesCount,
            author = AuthorResult.from(author, following)
        )

        fun from(
            article: Article,
            tags: List<String>,
            favorited: Boolean,
            favoritesCount: Int,
            author: ProfileResult,
            following: Boolean,
        ) = ArticleResult(
            slug = article.slug,
            title = article.title,
            description = article.description,
            body = article.body,
            tagList = tags,
            createdAt = article.createdAt,
            updatedAt = article.updatedAt,
            favorited = favorited,
            favoritesCount = favoritesCount,
            author = AuthorResult(
                author.username,
                author.bio,
                author.image,
                following
            )
        )
    }
}

data class AuthorResult(
    val username: String,
    val bio: String?,
    val image: String?,
    val following: Boolean,
) {
    companion object {
        fun from(author: Author, following: Boolean) = AuthorResult(
            username = author.username.value,
            bio = author.bio?.value,
            image = author.image?.fullPath,
            following = following
        )
    }
}