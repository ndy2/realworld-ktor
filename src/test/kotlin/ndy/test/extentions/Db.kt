package ndy.test.extentions

import io.kotest.core.listeners.AfterContainerListener
import io.kotest.core.listeners.BeforeContainerListener
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import ndy.infra.tables.ArticleTable.ArticleTags
import ndy.infra.tables.ArticleTable.Articles
import ndy.infra.tables.CommentTable.Comments
import ndy.infra.tables.FavoriteTable.Favorites
import ndy.infra.tables.FollowTable.Follows
import ndy.infra.tables.ProfileTable.Profiles
import ndy.infra.tables.TagTable.Tags
import ndy.infra.tables.UserTable.Users

object Db : BeforeContainerListener, AfterContainerListener {
    private val database = Database.connect(
            url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
            user = "root",
            driver = "org.h2.Driver",
            password = ""
    )

    override suspend fun beforeContainer(testCase: TestCase) = transaction(database) {
        SchemaUtils.create(Articles)
        SchemaUtils.create(ArticleTags)
        SchemaUtils.create(Comments)
        SchemaUtils.create(Favorites)
        SchemaUtils.create(Follows)
        SchemaUtils.create(Profiles)
        SchemaUtils.create(Tags)
        SchemaUtils.create(Users)
    }

    override suspend fun afterContainer(testCase: TestCase, result: TestResult) = transaction(database) {
        SchemaUtils.drop(Articles, ArticleTags, Comments, Favorites, Follows, Profiles, Tags, Users)
    }
}
