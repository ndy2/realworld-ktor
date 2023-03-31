package ndy.domain.article.application

import ndy.domain.article.comment.application.CommentResult
import ndy.domain.article.comment.application.CommentService
import ndy.domain.article.domain.Article
import ndy.domain.article.domain.ArticleRepository
import ndy.domain.article.favorite.application.FavoriteService
import ndy.domain.profile.application.ProfileService
import ndy.domain.profile.follow.application.FollowService
import ndy.domain.tag.application.TagResult
import ndy.domain.tag.application.TagService
import ndy.global.context.AuthenticatedUserContext
import ndy.global.context.userIdContext
import ndy.global.util.forbiddenIf
import ndy.global.util.newTransaction
import ndy.global.util.notFoundField
import ndy.global.util.now

class ArticleService(
    private val repository: ArticleRepository,
    private val tagService: TagService,
    private val profileService: ProfileService,
    private val followService: FollowService,
    private val commentService: CommentService,
    private val favoriteService: FavoriteService,
) {
    context (AuthenticatedUserContext/* optional = true */)
    suspend fun searchByCond(searchCond: ArticleSearchCond) = newTransaction {
        emptyList<ArticleResult>()
    }

    context (AuthenticatedUserContext)
    suspend fun getFeed() = newTransaction {
        emptyList<ArticleResult>()
    }

    context (AuthenticatedUserContext)
    suspend fun create(title: String, description: String, body: String, tagList: List<String>) = newTransaction {
        // 1. handle all tags - ask to tagService and get all list of tag Ids
        val tagIds = tagService.getOrSaveList(tagList)

        // 2. create article
        val authorId = profileId
        val article = Article.ofCreate(
            title = title,
            description = description,
            body = body,
            createdAt = now(),
            updatedAt = now(),
        )

        // 3. save it!
        repository.save(article, authorId, tagIds)

        // 4. find author (currentUser) profile
        val author = with(userIdContext()) { profileService.getByUserId() }

        // 5. return
        ArticleResult(
            slug = article.slug,
            title = article.title,
            description = article.description,
            body = article.body,
            tagList = tagList,
            createdAt = article.createdAt,
            updatedAt = article.updatedAt,
            favorited = false,
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
    suspend fun getBySlug(slug: String) = newTransaction {
        // 1. find article with author
        val (article, author) = repository.findBySlugWithAuthor(slug) ?: notFoundField(Article::slug, slug)

        // 2. check favorited & following
//        with(userIdContext())

        // 3. find all tags
        val tagResults = tagService.getByTagIds(emptyList()) // TODO requires tagIds

        // 3. return
        ArticleResult(
            slug = article.slug,
            title = article.title,
            description = article.description,
            body = article.body,
            tagList = tagResults.map(TagResult::name),
            createdAt = article.createdAt,
            updatedAt = article.updatedAt,
            favorited = false,
            favoritesCount = 0,
            author = AuthorResult(
                username = author.username.value,
                bio = author.bio?.value,
                image = author.image?.fullPath,
                following = false
            )
        )
    }

    context (AuthenticatedUserContext)
    suspend fun update(slug: String, title: String?, description: String?, body: String?) = newTransaction {
        // 1. update article with new slug
        val updateSlug = title?.let { getSlug(it) } ?: slug
        val (article, authorId) = repository.updateBySlug(slug, updateSlug, title, description, body)
            ?: notFoundField(Article::slug, slug)

        // 2. check updatable - is current user writer of the article
        forbiddenIf(profileId != authorId)

        // 3. get author (current user) profile
        val author = with(userIdContext()) { profileService.getByUserId() }

        // 4. check favorited

        // 5. find all tags

        // 6. return
        ArticleResult(
            slug = article.slug,
            title = article.title,
            description = article.description,
            body = article.body,
            tagList = listOf("dargons", "training"), // TODO - tagList, favorited, favoritesCount
            createdAt = article.createdAt,
            updatedAt = article.updatedAt,
            favorited = false,
            favoritesCount = 0,
            author = AuthorResult(
                username = author.username,
                bio = author.bio,
                image = author.image,
                following = false
            )
        )
    }

    context (AuthenticatedUserContext)
    suspend fun deleteBySlug(slug: String) = newTransaction {
        // 1. check article exists
        val (_, authorId) = repository.findBySlug(slug) ?: notFoundField(Article::slug, slug)

        // 2. check deletable - is current user writer of the article
        forbiddenIf(profileId != authorId)

        // 3. delete
        repository.deleteBySlug(slug)
    }

    context (AuthenticatedUserContext)
    suspend fun addComment(slug: String, body: String) = newTransaction {
        CommentResult(
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

    context (AuthenticatedUserContext /* optional = true */)
    suspend fun getComments(slug: String) = newTransaction {
        emptyList<CommentResult>()
    }

    context (AuthenticatedUserContext)
    suspend fun deleteComment(slug: String, commentId: ULong): Nothing = newTransaction {
        TODO("Not yet implemented")
    }

    context (AuthenticatedUserContext)
    suspend fun favorite(slug: String): Nothing = newTransaction {
        TODO("Not yet implemented")
    }

    context (AuthenticatedUserContext)
    suspend fun unfavorite(slug: String): Nothing = newTransaction {
        TODO("Not yet implemented")
    }
}

private fun getSlug(title: String) = title.lowercase().replace(" ", "-")
