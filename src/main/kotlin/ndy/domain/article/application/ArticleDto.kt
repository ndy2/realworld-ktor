package ndy.domain.article.application

import java.time.LocalDateTime

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
)

data class AuthorResult(
    val username: String,
    val bio: String,
    val image: String,
    val following: Boolean,
)