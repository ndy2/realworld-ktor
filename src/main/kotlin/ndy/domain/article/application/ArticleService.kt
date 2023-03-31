package ndy.domain.article.application

import ndy.domain.article.comment.application.CommentResult
import ndy.domain.article.comment.application.CommentService
import ndy.domain.article.domain.Article
import ndy.domain.article.domain.Article.Companion.createSlug
import ndy.domain.article.domain.ArticleRepository
import ndy.domain.article.favorite.application.FavoriteService
import ndy.domain.profile.application.ProfileService
import ndy.domain.profile.follow.application.FollowService
import ndy.domain.tag.application.TagService
import ndy.global.context.AuthenticatedUserContext
import ndy.global.context.profileIdContext
import ndy.global.context.userIdContext
import ndy.global.util.*

class ArticleService(
    private val repository: ArticleRepository,
    private val tagService: TagService,
    private val profileService: ProfileService,
    private val followService: FollowService,
    private val commentService: CommentService,
    private val favoriteService: FavoriteService,
) {
    context (AuthenticatedUserContext/* optional = true */)
    suspend fun searchByCond(searchCond: ArticleSearchCond) = requiresNewTransaction {
        listOf(
            ArticleResult(
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
        )
    }

    context (AuthenticatedUserContext)
    suspend fun getFeed() = requiresNewTransaction {
        listOf(
            ArticleResult(
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
        )
    }

    context (AuthenticatedUserContext)
    suspend fun create(title: String, description: String, body: String, tagList: List<String>) =
        requiresNewTransaction {
            // 1. handle all tags - ask to tagService and get all list of tag Ids
            val tagIds = tagService.getOrSaveList(tagList)

            // 2. create article
            val authorId = profileId
            val article = Article.ofCreate(
                title = title,
                description = description,
                body = body,
            )

            // 3. save it
            repository.save(article, authorId, tagIds)

            // 4. find author (currentUser) profile
            val author = with(userIdContext()) { profileService.getByUserId() }

            // 5. return
            ArticleResult.from(
                article = article,
                tags = tagList,
                favorited = false,
                favoritesCount = 0,
                author = author,
                following = false
            )
        }

    context (AuthenticatedUserContext /* optional = true */)
    suspend fun getBySlug(slug: String) = requiredTransaction {
        // 1. find article with author and tagIds
        val (article, author, tagIds) = repository
            .findWithAuthorAndTagIdsBySlug(slug)
            ?: notFoundField(Article::slug, slug)

        // 2. check favorited & get favoritesCount
        val favorited = with(profileIdContext()) {
            favoriteService.isFavorite(article.id)
        }
        val favoritesCount = favoriteService.getCount(article.id)

        // 3. check following
        val following = with(profileIdContext()) {
            if (author.id == profileId) false
            else followService.isFollowing(author.id)
        }

        // 4. find all tags
        val tagResults = tagService.getByTagIds(tagIds)

        // 5. return
        ArticleResult.from(
            article = article,
            tagResults = tagResults,
            favorited = favorited,
            favoritesCount = favoritesCount,
            author = author,
            following = following
        )
    }

    context (AuthenticatedUserContext)
    suspend fun update(slug: String, title: String?, description: String?, body: String?) = requiresNewTransaction {
        // 1. update article with new slug
        val updateSlug = title?.let { createSlug(it) } ?: slug
        val (_, authorId) = repository
            .updateBySlug(slug, updateSlug, title, description, body)
            ?: notFoundField(Article::slug, slug)

        // 2. check updatable - is current user writer of the article
        forbiddenIf(profileId != authorId)

        // 3. get by slug
        getBySlug(slug)
    }

    context (AuthenticatedUserContext)
    suspend fun deleteBySlug(slug: String) = requiresNewTransaction {
        // 1. check article exists
        val (_, authorId) = repository
            .findBySlug(slug)
            ?: notFoundField(Article::slug, slug)

        // 2. check deletable - is current user writer of the article
        forbiddenIf(profileId != authorId)

        // 3. delete
        repository.deleteBySlug(slug)
    }

    context (AuthenticatedUserContext)
    suspend fun addComment(slug: String, body: String) = requiresNewTransaction {
        // 1. check article exists
        val (article, _) = repository.findBySlug(slug)
            ?: notFoundField(Article::slug, slug)

        // 2. get author (current user)
        val author = with(userIdContext()) { profileService.getByUserId() }

        // 3. add comment
        val comment = with(profileIdContext()) { commentService.add(article.id, body) }

        // 4. return
        CommentResult.from(comment, author)
    }

    context (AuthenticatedUserContext /* optional = true */)
    suspend fun getComments(slug: String) = requiresNewTransaction {
        // 1. check articles exists
        val (article, _) = repository.findBySlug(slug)
            ?: notFoundField(Article::slug, slug)

        // 2. get all comments with its author

        // 3. get list of following

        // 4. zip and return

    }

    context (AuthenticatedUserContext)
    suspend fun deleteComment(slug: String, commentId: ULong) = requiresNewTransaction {
        // 1. check articles exists
        val (article, _) = repository.findBySlug(slug)
            ?: notFoundField(Article::slug, slug)

        // 2. delete it
    }

    context (AuthenticatedUserContext)
    suspend fun favorite(slug: String) = requiresNewTransaction {
        // 1. get article with author and tagIds
        val (article, author, tagIds) = repository
            .findWithAuthorAndTagIdsBySlug(slug)
            ?: notFoundField(Article::slug, slug)

        // 2. do favorite and get count
        val favoritesCount = with(profileIdContext()) {
            favoriteService.favorite(article.id)
            favoriteService.getCount(article.id)
        }

        // 3. check following
        val following = with(profileIdContext()) {
            if (author.id == profileId) false
            else followService.isFollowing(author.id)
        }

        // 4. find all tags
        val tagResults = tagService.getByTagIds(tagIds)

        // 5. return
        ArticleResult.from(
            article = article,
            tagResults = tagResults,
            favorited = true,
            favoritesCount = favoritesCount,
            author = author,
            following = following
        )
    }

    context (AuthenticatedUserContext)
    suspend fun unfavorite(slug: String) = requiresNewTransaction {
        // 1. get article with author and tagIds
        val (article, author, tagIds) = repository
            .findWithAuthorAndTagIdsBySlug(slug)
            ?: notFoundField(Article::slug, slug)

        // 2. do favorite and get count
        val favoritesCount = with(profileIdContext()) {
            favoriteService.unfavorite(article.id)
            favoriteService.getCount(article.id)
        }

        // 3. check following
        val following = with(profileIdContext()) {
            if (author.id == profileId) false
            else followService.isFollowing(author.id)
        }

        // 4. find all tags
        val tagResults = tagService.getByTagIds(tagIds)

        // 5. return
        ArticleResult.from(
            article = article,
            tagResults = tagResults,
            favorited = false,
            favoritesCount = favoritesCount,
            author = author,
            following = following
        )
    }
}
