package ndy.api.routers

import io.ktor.server.application.ApplicationCall
import io.ktor.server.routing.Route
import org.koin.ktor.ext.inject
import ndy.api.dto.ArticleCreateRequest
import ndy.api.dto.ArticleResponse
import ndy.api.dto.ArticleResponseList
import ndy.api.dto.ArticleUpdateRequest
import ndy.api.dto.CommentAddRequest
import ndy.api.dto.CommentResponse
import ndy.api.resources.Articles
import ndy.domain.article.application.ArticleSearchCond
import ndy.domain.article.application.ArticleService
import ndy.global.util.authenticatedDelete
import ndy.global.util.authenticatedGet
import ndy.global.util.authenticatedPost
import ndy.global.util.authenticatedPut
import ndy.global.util.extract
import ndy.global.util.noContent
import ndy.global.util.ok

fun Route.articleRouting() {
    val service by inject<ArticleService>()

    /**
     * List Articles
     * GET /api/articles
     */
    authenticatedGet<Articles>(optional = true) {
        // bind
        val queryParams = it
        val searchCond = ArticleSearchCond(
                tag = queryParams.tag,
                author = queryParams.author,
                favorited = queryParams.favorited,
                limit = queryParams.limit,
                offset = queryParams.offset
        )

        // action
        val resultList = service.searchByCond(searchCond)

        // response
        val responseList = resultList.map(ArticleResponse::ofResult)
        call.okArticleList(responseList)
    }

    /**
     * Feed Articles
     * GET /api/articles/feed
     */
    authenticatedGet<Articles.Feed> {
        // action
        val resultList = service.getFeed()

        // response
        val responseList = resultList.map(ArticleResponse::ofResult)
        call.okArticleList(responseList)
    }

    /**
     * Get Article
     * GET /api/articles/{slug}
     */
    authenticatedGet<Articles.Slug>(optional = true) {
        // bind
        val slug = it.slug

        // action
        val result = service.getBySlug(slug)

        // response
        val response = ArticleResponse.ofResult(result)
        call.okArticle(response)
    }

    /**
     * Create Article
     * POST /api/articles
     */
    authenticatedPost<Articles> {
        // bind
        val request = call.extract<ArticleCreateRequest>("article")

        // action
        val result = service.create(
                title = request.title,
                description = request.description,
                body = request.body,
                tagList = request.tagList
        )

        // response
        val response = ArticleResponse.ofResult(result)
        call.okArticle(response)
    }

    /**
     * Update Article
     * PUT /api/articles/{slug}
     */
    authenticatedPut<Articles.Slug> {
        // bind
        val slug = it.slug
        val request = call.extract<ArticleUpdateRequest>("article")

        // action
        val result = service.update(
                slug = slug,
                title = request.title,
                description = request.description,
                body = request.body
        )

        // response
        val response = ArticleResponse.ofResult(result)
        call.okArticle(response)
    }

    /**
     * Delete Article
     * DELETE /api/articles/{slug}
     */
    authenticatedDelete<Articles.Slug> {
        // bind
        val slug = it.slug

        // action
        service.deleteBySlug(slug)

        // response
        call.noContent()
    }

    /**
     * Add Comments to an Article
     * POST /api/articles/{slug}/comments
     */
    authenticatedPost<Articles.Slug.Comments> {
        // bind
        val slug = it.parent.slug
        val request = call.extract<CommentAddRequest>("comment")

        // action
        val result = service.addComment(slug, request.body)

        // response
        val response = CommentResponse.ofResult(result)
        call.okComment(response)
    }

    /**
     * Get Comments from an Article
     * GET /api/articles/{slug}/comments
     */
    authenticatedGet<Articles.Slug.Comments>(optional = true) {
        // bind
        val slug = it.parent.slug

        // action
        val resultList = service.getComments(slug)

        // response
        val responseList = resultList.map(CommentResponse::ofResult)
        call.okCommentList(responseList)
    }

    /**
     * Delete Comment
     * DELETE /api/articles/{slug}/comments/{id}
     */
    authenticatedDelete<Articles.Slug.Comments.Id> {
        // bind
        val slug = it.parent.parent.slug
        val id = it.id.toULong()

        // action
        service.deleteComment(slug, id)

        // response
        call.noContent()
    }

    /**
     * Favorite Article
     * POST /api/articles/{slug}/favorite
     */
    authenticatedPost<Articles.Slug.Favorite> {
        // bind
        val slug = it.parent.slug

        // action
        val result = service.favorite(slug)

        // response
        val response = ArticleResponse.ofResult(result)
        call.okArticle(response)
    }

    /**
     * Unfavorite Article
     * DELETE /api/articles/{slug}/favorite
     */
    authenticatedDelete<Articles.Slug.Favorite> {
        // bind
        val slug = it.parent.slug

        // action
        val result = service.unfavorite(slug)

        // response
        val response = ArticleResponse.ofResult(result)
        call.okArticle(response)
    }
}

private suspend inline fun ApplicationCall.okArticle(response: ArticleResponse) {
    ok(mapOf("article" to response))
}

private suspend inline fun ApplicationCall.okArticleList(responseList: List<ArticleResponse>) {
    ok(ArticleResponseList(responseList, responseList.size))
}

private suspend inline fun ApplicationCall.okComment(response: CommentResponse) {
    ok(mapOf("comment" to response))
}

private suspend inline fun ApplicationCall.okCommentList(responseList: List<CommentResponse>) {
    ok(mapOf("comments" to responseList))
}
