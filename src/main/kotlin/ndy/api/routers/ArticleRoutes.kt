package ndy.api.routers

import io.ktor.server.application.*
import io.ktor.server.routing.*
import ndy.api.dto.*
import ndy.api.resources.Articles
import ndy.domain.article.application.ArticleSearchCond
import ndy.domain.article.application.ArticleService
import ndy.global.util.*
import org.koin.ktor.ext.inject

fun Route.articleRouting() {

    val service by inject<ArticleService>()

    authenticatedGet<Articles>(optional = true) {
        // bind
        val queryParams = it
        val searchCond = ArticleSearchCond(
            tag = queryParams.tag,
            author = queryParams.author,
            favorited = queryParams.favorited,
            limit = queryParams.limit,
            offset = queryParams.offset,
        )

        // action
        val resultList = service.searchByCond(searchCond)

        // response
        val responseList = resultList.map(ArticleResponse::ofResult)
        call.okArticleList(responseList)
    }

    authenticatedGet<Articles.Feed> {
        // action
        val resultList = service.getFeed()

        // response
        val responseList = resultList.map(ArticleResponse::ofResult)
        call.okArticleList(responseList)
    }

    authenticatedGet<Articles.Slug>(optional = true) {
        // bind
        val slug = it.slug

        // action
        val result = service.getBySlug(slug)

        // response
        val response = ArticleResponse.ofResult(result)
        call.okArticle(response)
    }

    authenticatedPost<Articles> {
        // bind
        val request = call.extract<ArticleCreateRequest>("article")

        // action
        val result = service.create(
            title = request.title,
            description = request.description,
            body = request.body,
            tagList = request.tagList,
        )

        // response
        val response = ArticleResponse.ofResult(result)
        call.okArticle(response)
    }

    authenticatedPut<Articles.Slug> {
        // bind
        val request = call.extract<ArticleUpdateRequest>("article")

        // action
        val result = service.update(
            title = request.title,
            description = request.description,
            body = request.body,
        )

        // response
        val response = ArticleResponse.ofResult(result)
        call.okArticle(response)
    }

    authenticatedDelete<Articles.Slug> {
        // bind
        val slug = it.slug

        // action
        service.deleteBySlug(slug)

        // response
        call.noContent()
    }

    authenticatedPost<Articles.Slug.Comments> {
        // bind
        val slug = it.parent.slug
        val request = call.extract<CommentAddRequest>("comment")

        // action
        val result = service.addComment(slug, request.body)

        // response
        call.okComment(result)
    }

    authenticatedGet<Articles.Slug.Comments>(optional = true) {
        // bind
        val slug = it.parent.slug

        // action
        val resultList = service.getComments(slug)

        // response
        val responseList = resultList.map(CommentResponse::ofResult)
        call.okCommentList(responseList)
    }

    authenticatedDelete<Articles.Slug.Comments.Id> {
        // bind
        val slug = it.parent.parent.slug
        val id = it.id.toULong()

        // action
        service.deleteComment(slug, id)

        // response
        call.noContent()
    }

    authenticatedPost<Articles.Slug.Favorite> {
        // bind
        val slug = it.parent.slug

        // action
        val result = service.favorite(slug)

        // response
        val response = ArticleResponse.ofResult(result)
        call.okArticle(response)
    }

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

private suspend inline fun <reified T> ApplicationCall.okArticle(response: T) {
    ok(mapOf("article" to response))
}

private suspend inline fun <reified T> ApplicationCall.okArticleList(responseList: List<T>) {
    ok(mapOf("articles" to responseList, "articlesCount" to responseList.size))
}

private suspend inline fun <reified T> ApplicationCall.okComment(response: T) {
    ok(mapOf("comment" to response))
}

private suspend inline fun <reified T> ApplicationCall.okCommentList(responseList: List<T>) {
    ok(mapOf("comment" to responseList))
}

