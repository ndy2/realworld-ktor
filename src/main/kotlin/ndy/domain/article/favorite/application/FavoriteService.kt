package ndy.domain.article.favorite.application

import ndy.domain.article.domain.ArticleId
import ndy.domain.article.favorite.domain.FavoriteRepository
import ndy.domain.profile.domain.Username
import ndy.global.context.AuthenticatedUserContext
import ndy.global.util.mandatoryTransaction

class FavoriteService(
    private val repository: FavoriteRepository
) {
    context (AuthenticatedUserContext)
    suspend fun isFavorite(articleId: ArticleId) = mandatoryTransaction {
        repository.exists(profileId, articleId)
    }

    context (AuthenticatedUserContext)
    suspend fun favorite(articleId: ArticleId) = mandatoryTransaction {
        repository.save(profileId, articleId)
    }

    context (AuthenticatedUserContext)
    suspend fun unfavorite(articleId: ArticleId) = mandatoryTransaction {
        repository.delete(profileId, articleId)
    }

    suspend fun getCount(id: ArticleId) = mandatoryTransaction {
        repository.countByArticleId(id).toInt()
    }

    suspend fun getAllFavoritedArticleIds(username: Username) = mandatoryTransaction{
        repository.findAllFavoritedArticleIds(username)
    }
}