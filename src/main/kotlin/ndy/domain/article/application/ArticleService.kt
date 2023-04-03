package ndy.domain.article.application

import ndy.domain.article.comment.application.CommentResult
import ndy.domain.article.comment.application.CommentService
import ndy.domain.article.comment.domain.Author
import ndy.domain.article.comment.domain.CommentId
import ndy.domain.article.domain.Article
import ndy.domain.article.domain.Article.Companion.createSlug
import ndy.domain.article.domain.ArticleRepository
import ndy.domain.article.domain.AuthorId
import ndy.domain.article.favorite.application.FavoriteService
import ndy.domain.profile.application.ProfileService
import ndy.domain.profile.domain.Username
import ndy.domain.profile.follow.application.FollowService
import ndy.domain.tag.application.TagService
import ndy.global.context.AuthenticatedUserContext
import ndy.global.exception.FieldNotFoundException
import ndy.global.util.forbiddenIf
import ndy.global.util.notFound
import ndy.global.util.now
import ndy.global.util.transactional
import ndy.global.util.unzip

class ArticleService(
    private val repository: ArticleRepository,
    private val tagService: TagService,
    private val profileService: ProfileService,
    private val followService: FollowService,
    private val commentService: CommentService,
    private val favoriteService: FavoriteService
) {
    context (AuthenticatedUserContext/* optional = true */)
    suspend fun searchByCond(searchCond: ArticleSearchCond) = transactional {
        // 1. setup find conditions
        val favoritedArticleIds = searchCond.favorited?.let { favoriteService.getAllFavoritedArticleIds(Username(it)) }
        val tagId = searchCond.tag?.let { tagService.getIdByName(it) }
        val searchAuthor = searchCond.author?.let { authorResultOrNull(it) }
        if (searchCond.author != null && searchAuthor == null) return@transactional emptyList() // by postman api spec

        // 2. find article with author and tagIds
        val (articles, authors, tagIdsList) = repository.findByCond(
            idFilter = favoritedArticleIds,
            tagId = tagId,
            authorId = searchAuthor?.id?.let { AuthorId(it) },
            offset = searchCond.offset,
            limit = searchCond.limit
        ).unzip()

        // 3. collect additional infos
        val tagsList = tagIdsList.map { tagService.getByTagIds(it, firstTagId = tagId) }
        val favoritedList =
            if (authenticated) {
                articles.map { favoriteService.isFavorite(it.id) }
            } else {
                falseList(articles.size)
            }
        val favoriteCountsList = articles.map { favoriteService.getCount(it.id) }
        val followings =
            if (authenticated) {
                followService.isFollowingList(authors.map { it.id })
            } else {
                falseList(articles.size)
            }

        // 4. return
        (articles.indices).map {
            ArticleResult.from(
                article = articles[it],
                tagResults = tagsList[it],
                favorited = favoritedList[it],
                favoritesCount = favoriteCountsList[it],
                author = authors[it],
                following = followings[it]
            )
        }
    }

    // TODO - get feed!
    context (AuthenticatedUserContext)
    suspend fun getFeed() = transactional {
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
        transactional {
            // 1. handle all tags - ask to tagService and get all list of tag Ids
            val tagIds = tagService.getOrSaveList(tagList)

            // 2. create article
            val authorId = profileId
            val article = Article.ofCreate(
                title = title,
                description = description,
                body = body
            )

            // 3. save it
            repository.save(article, authorId, tagIds)

            // 4. find author (currentUser) profile
            val author = profileService.getByUserId(userId)

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
    suspend fun getBySlug(slug: String) = transactional {
        // 1. find article with author and tagIds
        val (article, author, tagIds) = repository
            .findWithAuthorAndTagIdsBySlug(slug)
            ?: notFound(Article::slug, slug)

        // 2. check favorited & get favoritesCount
        val favorited = favoriteService.isFavorite(article.id)
        val favoritesCount = favoriteService.getCount(article.id)

        // 3. check following
        val following = followService.isFollowing(author.id)

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
    suspend fun update(slug: String, title: String?, description: String?, body: String?) = transactional {
        // 1. update article with new slug
        val updateSlug = title?.let { createSlug(it) } ?: slug
        val (_, authorId) = repository
            .updateBySlug(slug, updateSlug, title, description, body)
            ?: notFound(Article::slug, slug)

        // 2. check updatable - is current user writer of the article
        forbiddenIf(profileId != authorId)

        // 3. get by slug
        getBySlug(slug)
    }

    context (AuthenticatedUserContext)
    suspend fun deleteBySlug(slug: String) = transactional {
        // 1. check article exists
        val (_, authorId) = repository
            .findBySlug(slug)
            ?: notFound(Article::slug, slug)

        // 2. check deletable - is current user writer of the article
        forbiddenIf(profileId != authorId)

        // 3. delete
        repository.deleteBySlug(slug)
    }

    context (AuthenticatedUserContext)
    suspend fun addComment(slug: String, body: String) = transactional {
        // 1. check article exists
        val (article, _) = repository.findBySlug(slug)
            ?: notFound(Article::slug, slug)

        // 2. get author (current user)
        val author = profileService.getByUserId(userId)

        // 3. add comment
        val comment = commentService.add(article.id, body)

        // 4. return
        CommentResult.from(comment, author)
    }

    context (AuthenticatedUserContext /* optional = true */)
    suspend fun getComments(slug: String) = transactional {
        // 1. setup -  check articles exists
        val (article, _) = repository.findBySlug(slug)
            ?: notFound(Article::slug, slug)

        // 2. get all comments with its author
        val (comments, authors) = commentService.getWithAuthorByArticleId(article.id).unzip()

        // 3. get additional infos
        val followings =
            if (authenticated) {
                followService.isFollowingList(authors.map(Author::id))
            } else {
                falseList(comments.size)
            }

        // 4. return
        (comments.indices).map { CommentResult.from(comments[it], authors[it], followings[it]) }
    }

    context (AuthenticatedUserContext)
    suspend fun deleteComment(slug: String, commentId: ULong) = transactional {
        // 1. check articles exists
        val (article, _) = repository.findBySlug(slug)
            ?: notFound(Article::slug, slug)

        // 2. delete it
        commentService.delete(CommentId(commentId), article.id)
    }

    context (AuthenticatedUserContext)
    suspend fun favorite(slug: String) = transactional {
        // 1. get article with author and tagIds
        val (article, author, tagIds) = repository
            .findWithAuthorAndTagIdsBySlug(slug)
            ?: notFound(Article::slug, slug)

        // 2. do favorite and get count
        favoriteService.favorite(article.id)
        val favoritesCount = favoriteService.getCount(article.id)

        // 3. check following
        val following = followService.isFollowing(author.id)

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
    suspend fun unfavorite(slug: String) = transactional {
        // 1. get article with author and tagIds
        val (article, author, tagIds) = repository
            .findWithAuthorAndTagIdsBySlug(slug)
            ?: notFound(Article::slug, slug)

        // 2. do unfavorite and get count
        favoriteService.unfavorite(article.id)
        val favoritesCount = favoriteService.getCount(article.id)

        // 3. check following
        val following = followService.isFollowing(author.id)

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

    context (AuthenticatedUserContext)
    private suspend fun authorResultOrNull(authorName: String) = try {
        profileService.getByUsername(authorName)
    } catch (e: FieldNotFoundException) {
        null
    }

    // dummy false list for build List of not authenticated user's following/favorited
    private fun falseList(size: Int) = List(size) { false }
}
