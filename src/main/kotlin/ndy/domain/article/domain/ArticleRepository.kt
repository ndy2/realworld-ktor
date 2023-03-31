package ndy.domain.article.domain

interface ArticleRepository {

    fun save(article: Article): Article
}