package ndy.domain.article.favorite.domain

import ndy.domain.article.domain.ArticleId
import ndy.domain.profile.domain.ProfileId

interface FavoriteRepository {


    suspend fun save(profileId: ProfileId, articleId: ArticleId)
    suspend fun delete(profileId: ProfileId, articleId: ArticleId)
    suspend fun exists(profileId: ProfileId, articleId: ArticleId): Boolean
}