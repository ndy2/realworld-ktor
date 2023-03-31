package ndy.domain.article.domain

import kotlinx.datetime.LocalDateTime
import ndy.domain.profile.domain.Profile
import ndy.domain.tag.domain.TagId

data class Article(
    val id: ArticleId = ArticleId(0u),
    val slug: String,
    val title: String,
    val description: String,
    val body: String,
    val tagIds: List<TagId> = emptyList(),
    val author: Profile? = null, // TODO - removed
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)

@JvmInline
value class ArticleId(val value: ULong)
