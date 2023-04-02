package ndy.domain.article.favorite.domain

import ndy.domain.article.domain.ArticleId
import ndy.domain.profile.domain.ProfileId
import ndy.domain.profile.domain.Username

interface FavoriteRepository {

    suspend fun save(profileId: ProfileId, articleId: ArticleId)
    suspend fun delete(profileId: ProfileId, articleId: ArticleId)
    suspend fun exists(profileId: ProfileId, articleId: ArticleId): Boolean
    suspend fun countByArticleId(articleId: ArticleId): Long
    suspend fun findAllFavoritedArticleIds(username: Username) : List<ArticleId>
}