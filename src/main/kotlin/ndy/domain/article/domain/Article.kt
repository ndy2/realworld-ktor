package ndy.domain.article.domain

import ndy.domain.profile.domain.Profile
import ndy.domain.profile.domain.ProfileId
import ndy.global.util.notUsed
import ndy.global.util.now

import kotlinx.datetime.LocalDateTime

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
    val updatedAt: LocalDateTime
) {
    companion object {
        fun ofCreate(
            title: String,
            description: String,
            body: String
        ) = Article(
            id = ArticleId(0u),
            slug = title.lowercase().replace(" ", "-"),
            title = title,
            description = description,
            body = body,
            createdAt = now(),
            updatedAt = notUsed
        )

        fun createSlug(title: String) = title.lowercase().replace(" ", "-")
    }
}
