package ndy.domain.article.favorite.application

import ndy.domain.article.domain.ArticleId
import ndy.domain.article.favorite.domain.FavoriteRepository
import ndy.global.context.ProfileIdContext
import ndy.global.util.mandatoryTransaction

class FavoriteService(
    private val repository: FavoriteRepository
) {
    context (ProfileIdContext)
    suspend fun isFavorite(articleId: ArticleId) = mandatoryTransaction {
        repository.exists(profileId, articleId)
    }

    context (ProfileIdContext)
    suspend fun favorite(articleId: ArticleId) = mandatoryTransaction {
        repository.save(profileId, articleId)
    }

    context (ProfileIdContext)
    suspend fun unfavorite(articleId: ArticleId) = mandatoryTransaction {
        repository.delete(profileId, articleId)
    }

    suspend fun getCount(id: ArticleId) = mandatoryTransaction {
        repository.countByArticleId(id).toInt()
    }
}