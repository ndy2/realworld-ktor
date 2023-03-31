package ndy.domain.article.domain

import ndy.domain.tag.domain.TagId

typealias ArticleWithAuthorId = Pair<Article, AuthorId>
typealias ArticleWithAuthor = Pair<Article, Author>
typealias ArticleWithTagIds = Pair<Article, List<TagId>>

interface ArticleRepository {

    fun save(article: Article, authorId: AuthorId, tagIds: List<TagId>): Article

    fun findBySlugWithAuthor(slug: String): ArticleWithAuthor? // TODO requires TagIds TOO!

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
    ): ArticleWithAuthorId?

    fun findBySlug(slug: String): ArticleWithAuthorId?

    fun deleteBySlug(slug: String): Int
}