package ndy.domain.article.domain

import ndy.domain.profile.domain.ProfileId

interface ArticleRepository {

    fun save(article: Article, authorId: ProfileId): Article
    fun findBySlugWithAuthor(slug: String): Article?
}