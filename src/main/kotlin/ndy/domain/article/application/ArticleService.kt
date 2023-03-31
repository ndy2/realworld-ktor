package ndy.domain.article.application

import ndy.domain.article.comment.CommentResult
import ndy.global.context.AuthenticatedUserContext
import java.time.LocalDateTime

class ArticleService {

    context (AuthenticatedUserContext/* optional = true */)
    fun searchByCond(searchCond: ArticleSearchCond): List<ArticleResult> {
        return emptyList()
    }

    context (AuthenticatedUserContext)
    fun getFeed(): List<ArticleResult> {
        return emptyList()
    }

    fun create(title: String, description: String, body: String, tagList: List<String>): ArticleResult {
        return ArticleResult(
            slug = "how-to-train-your-dragon",
            title = "How to train your dragon",
            description = "Ever wonder how?",
            body = "It takes a Jacobian",
            tagList = listOf("dargons", "training"),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
            favorited = false,
            favoritesCount = 0,
            author = AuthorResult(
                username = "jake",
                bio = "I work at statefarm",
                image = "https://i.stack.imgur.com/xHWG8.jpg",
                following = false
            )
        )
    }

    fun getBySlug(slug: String): ArticleResult {
        return ArticleResult(
            slug = "how-to-train-your-dragon",
            title = "How to train your dragon",
            description = "Ever wonder how?",
            body = "It takes a Jacobian",
            tagList = listOf("dargons", "training"),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
            favorited = false,
            favoritesCount = 0,
            author = AuthorResult(
                username = "jake",
                bio = "I work at statefarm",
                image = "https://i.stack.imgur.com/xHWG8.jpg",
                following = false
            )
        )
    }

    fun update(title: String?, description: String?, body: String?): ArticleResult {
        return ArticleResult(
            slug = "how-to-train-your-dragon",
            title = "How to train your dragon",
            description = "Ever wonder how?",
            body = "It takes a Jacobian",
            tagList = listOf("dargons", "training"),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
            favorited = false,
            favoritesCount = 0,
            author = AuthorResult(
                username = "jake",
                bio = "I work at statefarm",
                image = "https://i.stack.imgur.com/xHWG8.jpg",
                following = false
            )
        )
    }

    fun deleteBySlug(slug: String) {
        TODO("Not yet implemented")
    }

    fun addComment(slug: String, body: String): CommentResult {
        return CommentResult(
            1u,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
            body = "It takes a Jacobian",
            author = CommentResult.AuthorResult(
                username = "jake",
                bio = "I work at statefarm",
                image = "https://i.stack.imgur.com/xHWG8.jpg",
                following = false
            )
        )
    }

    fun getComments(slug: String): List<CommentResult> {
        return emptyList()
    }

    fun deleteComment(slug: String, commentId: ULong) {
        TODO("Not yet implemented")
    }

    fun favorite(slug: String): ArticleResult {
        TODO("Not yet implemented")
    }

    fun unfavorite(slug: String): ArticleResult {
        TODO("Not yet implemented")
    }
}