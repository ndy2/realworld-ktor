package ndy.domain.article.domain

import ndy.domain.profile.domain.ProfileId

/**
 * implies single article row with its author id
 * first - article
 * second - authorId
 */
typealias ArticleRow = Pair<Article, ProfileId>

interface ArticleRepository {

    fun save(article: Article, authorId: ProfileId): Article
    fun findBySlugWithAuthor(slug: String): Article?

    /**
     * @param slug target article slug
     * @param others update fields
     * @return p?.first article
     * @return p?.second authorId
     */
    fun updateBySlug(
        slug: String,
        updatedSlug: String,
        title: String?,
        description: String?,
        body: String?
    ): ArticleRow?

    fun findRowBySlug(slug: String) : ArticleRow?

    fun deleteBySlug(slug: String)
}