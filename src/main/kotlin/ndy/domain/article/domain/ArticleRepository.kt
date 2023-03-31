package ndy.domain.article.domain

typealias ArticleWithAuthor = Pair<Article, Author>

interface ArticleRepository {

    fun save(article: Article, authorId: AuthorId): Article

    fun findBySlugWithAuthor(slug: String): ArticleWithAuthor?

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
    ): Article?

    fun findBySlug(slug: String): Article?

    fun deleteBySlug(slug: String)
}