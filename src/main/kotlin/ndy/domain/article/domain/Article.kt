package ndy.domain.article.domain

import kotlinx.datetime.LocalDateTime
import ndy.domain.profile.domain.Profile
import ndy.domain.profile.domain.ProfileId

// Profile implies Author @ profile domain
typealias Author = Profile
typealias AuthorId = ProfileId

data class Article(
    val id: ArticleId,
    val slug: String,
    val title: String,
    val description: String,
    val body: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
) {
    companion object {
        fun ofCreate(
            title: String,
            description: String,
            body: String,
            createdAt: LocalDateTime,
            updatedAt: LocalDateTime
        ) = Article(
            id = ArticleId(0u),
            slug = title.lowercase().replace(" ", "-"),
            title = title,
            description = description,
            body = body,
            createdAt = createdAt,
            updatedAt = updatedAt,
        )
    }
}

@JvmInline
value class ArticleId(val value: ULong)
