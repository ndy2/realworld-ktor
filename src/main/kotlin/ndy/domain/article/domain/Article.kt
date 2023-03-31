package ndy.domain.article.domain

import kotlinx.datetime.LocalDateTime
import ndy.domain.profile.domain.Profile
import ndy.domain.profile.domain.ProfileId
import ndy.domain.tag.domain.TagId

// Profile implies Author @ profile domain
typealias Author = Profile
typealias AuthorId = ProfileId

data class Article(
    val id: ArticleId = ArticleId(0u),
    val slug: String,
    val title: String,
    val description: String,
    val body: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)

@JvmInline
value class ArticleId(val value: ULong)
