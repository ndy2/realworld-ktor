package ndy.api.routers

import io.ktor.server.routing.*
import ndy.api.resources.Articles
import ndy.global.util.authenticatedDelete
import ndy.global.util.authenticatedGet
import ndy.global.util.authenticatedPost
import ndy.global.util.authenticatedPut

fun Route.articleRouting() {


    authenticatedPost<Articles> {

    }

    authenticatedPut<Articles.Slug> {

    }

    authenticatedDelete<Articles.Slug> {

    }

    authenticatedPost<Articles.Slug.Comments> {

    }

    authenticatedGet<Articles.Slug.Comments>(optional = true) {

    }

    authenticatedDelete<Articles.Slug.Comments.Id> {

    }

    authenticatedPost<Articles.Slug.Favorite> {

    }

    authenticatedDelete<Articles.Slug.Favorite> {

    }
}
