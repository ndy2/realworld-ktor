package ndy.infra.tables

import ndy.domain.article.domain.ArticleId
import ndy.domain.article.favorite.domain.FavoriteRepository
import ndy.domain.profile.domain.ProfileId
import ndy.infra.tables.ArticleTable.Articles
import ndy.infra.tables.ProfileTable.Profiles
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select

object FavoriteTable : FavoriteRepository {

    object Favorites : Table() {
        val profileId = ulong("profile_id").references(Profiles.id)
        val articleId = ulong("article_id").references(Articles.id)
    }

    override suspend fun save(profileId: ProfileId, articleId: ArticleId) {
        Favorites.insert {
            it[Favorites.profileId] = profileId.value
            it[Favorites.articleId] = articleId.value
        }
    }

    override suspend fun delete(profileId: ProfileId, articleId: ArticleId) {
        Favorites.deleteWhere {
            Favorites.profileId eq profileId.value
            Favorites.articleId eq articleId.value
        }
    }

    override suspend fun exists(profileId: ProfileId, articleId: ArticleId) = Favorites
        .select {
            Favorites.profileId eq profileId.value
            Favorites.articleId eq articleId.value
        }
        .empty().not()

    override suspend fun countByArticleId(articleId: ArticleId) = Favorites
        .select { Favorites.articleId eq articleId.value }
        .count()
}