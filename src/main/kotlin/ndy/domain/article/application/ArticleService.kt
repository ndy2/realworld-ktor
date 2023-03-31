package ndy.domain.article.application

import ndy.domain.article.comment.application.CommentResult
import ndy.domain.article.comment.application.CommentService
import ndy.domain.article.domain.Article
import ndy.domain.article.domain.ArticleRepository
import ndy.domain.tag.application.TagService
import ndy.global.context.AuthenticatedUserContext
import ndy.global.util.now

class ArticleService(
    private val repository: ArticleRepository,
    private val commentService: CommentService,
    private val tagService: TagService,
) {
    context (AuthenticatedUserContext/* optional = true */)
    suspend fun searchByCond(searchCond: ArticleSearchCond): List<ArticleResult> {
        return emptyList()
    }

    context (AuthenticatedUserContext)
    suspend fun getFeed(): List<ArticleResult> {
        return emptyList()
    }

    context (AuthenticatedUserContext)
    suspend fun create(title: String, description: String, body: String, tagList: List<String>): ArticleResult {
        // 1. handle all tags
        // - ask to tagService and get all list of tag Ids
        val tagIds = tagService.getOrSaveList(tagList)

        // 2. create article
        Article(
            slug = title.lowercase().replace(" ", "-"),
            title = title,
            description = description,
            body = body,
            createdAt = now(),
            updatedAt = now(),
            tagIds = tagIds
        )

        // 3. save it!


        return ArticleResult(
            slug = "how-to-train-your-dragon",
            title = "How to train your dragon",
            description = "Ever wonder how?",
            body = "It takes a Jacobian",
            tagList = listOf("dargons", "training"),
            createdAt = now(),
            updatedAt = now(),
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

    suspend fun getBySlug(slug: String): ArticleResult {
        return ArticleResult(
            slug = "how-to-train-your-dragon",
            title = "How to train your dragon",
            description = "Ever wonder how?",
            body = "It takes a Jacobian",
            tagList = listOf("dargons", "training"),
            createdAt = now(),
            updatedAt = now(),
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

    suspend fun update(title: String?, description: String?, body: String?): ArticleResult {
        return ArticleResult(
            slug = "how-to-train-your-dragon",
            title = "How to train your dragon",
            description = "Ever wonder how?",
            body = "It takes a Jacobian",
            tagList = listOf("dargons", "training"),
            createdAt = now(),
            updatedAt = now(),
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

    suspend fun deleteBySlug(slug: String) {
        TODO("Not yet implemented")
    }

    suspend fun addComment(slug: String, body: String): CommentResult {
        return CommentResult(
            1u,
            createdAt = now(),
            updatedAt = now(),
            body = "It takes a Jacobian",
            author = CommentResult.AuthorResult(
                username = "jake",
                bio = "I work at statefarm",
                image = "https://i.stack.imgur.com/xHWG8.jpg",
                following = false
            )
        )
    }

    suspend fun getComments(slug: String): List<CommentResult> {
        return emptyList()
    }

    suspend fun deleteComment(slug: String, commentId: ULong) {
        TODO("Not yet implemented")
    }

    suspend fun favorite(slug: String): ArticleResult {
        TODO("Not yet implemented")
    }

    suspend fun unfavorite(slug: String): ArticleResult {
        TODO("Not yet implemented")
    }
}