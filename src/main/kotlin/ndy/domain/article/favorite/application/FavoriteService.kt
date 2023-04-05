package ndy.domain.article.favorite.application

import ndy.domain.article.domain.ArticleId
import ndy.domain.article.favorite.domain.FavoriteRepository
import ndy.domain.profile.domain.Username
import ndy.global.security.Principal
import ndy.global.util.Propagation.MANDATORY
import ndy.global.util.transactional
import ndy.ktor.context.auth.AuthenticationContext

class FavoriteService(
        private val repository: FavoriteRepository
) {
    context (AuthenticationContext<Principal>)
    suspend fun isFavorite(articleId: ArticleId) = transactional(MANDATORY) {
        repository.exists(principal.profileId, articleId)
    }

    context (AuthenticationContext<Principal>)
    suspend fun favorite(articleId: ArticleId) = transactional(MANDATORY) {
        repository.save(principal.profileId, articleId)
    }

    context (AuthenticationContext<Principal>)
    suspend fun unfavorite(articleId: ArticleId) = transactional(MANDATORY) {
        repository.delete(principal.profileId, articleId)
    }

    suspend fun getCount(id: ArticleId) = transactional(MANDATORY) {
        repository.countByArticleId(id).toInt()
    }

    suspend fun getAllFavoritedArticleIds(username: Username) = transactional(MANDATORY) {
        repository.findAllFavoritedArticleIds(username)
    }
}
