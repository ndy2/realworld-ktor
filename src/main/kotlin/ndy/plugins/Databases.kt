package ndy.plugins

import de.sharpmind.ktor.EnvConfig
import io.ktor.server.application.*
import ndy.infra.tables.ArticleTable.ArticleTags
import ndy.infra.tables.ArticleTable.Articles
import ndy.infra.tables.CommentTable.Comments
import ndy.infra.tables.FavoriteTable.Favorites
import ndy.infra.tables.FollowTable.Follows
import ndy.infra.tables.ProfileTable.Profiles
import ndy.infra.tables.TagTable.Tags
import ndy.infra.tables.UserTable.Users
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * configure database with Exposed!
 *
 * reference - https://ktor.io/docs/interactive-website-add-persistence.html#connect_db
 */
fun Application.configureDatabases() {
    val database = Database.connect(
        url = EnvConfig.getString("database.url"),
        user = EnvConfig.getString("database.user"),
        driver = EnvConfig.getString("database.driver"),
        password = EnvConfig.getString("database.password")
    )

    transaction(database) {
        SchemaUtils.create(Articles)
        SchemaUtils.create(ArticleTags)
        SchemaUtils.create(Comments)
        SchemaUtils.create(Follows)
        SchemaUtils.create(Favorites)
        SchemaUtils.create(Profiles)
        SchemaUtils.create(Tags)
        SchemaUtils.create(Users)
    }
}
