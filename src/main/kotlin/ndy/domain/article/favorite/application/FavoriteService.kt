package ndy.domain.article.favorite.application

import ndy.domain.article.domain.ArticleId
import ndy.global.context.ProfileIdContext
import ndy.global.util.mandatoryTransaction

class FavoriteService {

    context (ProfileIdContext)
    suspend fun isFavorite(articleId: ArticleId) = mandatoryTransaction {
        true
    }


    suspend fun getCount(id: ArticleId) = mandatoryTransaction {
        1
    }

    context (ProfileIdContext)
    suspend fun favorite(id: ArticleId) = mandatoryTransaction {

    }

    context (ProfileIdContext)
    suspend fun unfavorite(id: ArticleId) = mandatoryTransaction {

    }
}