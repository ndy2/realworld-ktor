package ndy.domain.article.application

import ndy.domain.article.comment.application.CommentResult
import ndy.domain.article.comment.application.CommentService
import ndy.domain.article.domain.Article
import ndy.domain.article.domain.ArticleRepository
import ndy.domain.profile.application.ProfileService
import ndy.domain.tag.application.TagService
import ndy.global.context.AuthenticatedUserContext
import ndy.global.context.userIdContext
import ndy.global.util.notFoundField
import ndy.global.util.now

class ArticleService(
    private val repository: ArticleRepository,
    private val profileService: ProfileService,
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
        // 1. handle all tags - ask to tagService and get all list of tag Ids
        val tagIds = tagService.getOrSaveList(tagList)

        // 2. create article
        val slug = title.lowercase().replace(" ", "-")
        val article = Article(
            slug = slug,
            title = title,
            description = description,
            body = body,
            createdAt = now(),
            updatedAt = now(),
            tagIds = tagIds
        )

        // 3. save it!
        repository.save(article, profileId)

        // 4. find author profile
        val author = with(userIdContext(userId)) { profileService.getByUserId() }

        return ArticleResult(
            slug = article.slug,
            title = article.title,
            description = article.description,
            body = article.body,
            tagList = tagList,
            createdAt = article.createdAt,
            updatedAt = article.updatedAt,
            favorited = false, // TODO - favorited, favoritesCount, author,following
            favoritesCount = 0,
            author = AuthorResult(
                username = author.username,
                bio = author.bio,
                image = author.image,
                following = false
            )
        )
    }

    context (AuthenticatedUserContext /* optional = true */)
    suspend fun getBySlug(slug: String): ArticleResult {
        // 1. find article with author
        val article = repository.findBySlugWithAuthor(slug) ?: notFoundField(Article::slug, slug)
        require(article.author != null)

        // 2. check favorited & following

        // 3. find all tags

        // 3. return
        return ArticleResult(
            slug = article.slug,
            title = article.title,
            description = article.description,
            body = article.body,
            tagList = listOf("dargons", "training"), // TODO - tagList, favorited, favoritesCount, author.following
            createdAt = article.createdAt,
            updatedAt = article.updatedAt,
            favorited = false,
            favoritesCount = 0,
            author = AuthorResult(
                username = article.author.username.value,
                bio = article.author.bio?.value,
                image = article.author.image?.fullPath,
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