package ndy.plugins

import de.sharpmind.ktor.EnvConfig
import io.ktor.server.application.*
import ndy.infra.tables.FollowTable
import ndy.infra.tables.ProfileTable
import ndy.infra.tables.UserTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * configure database with Exposed!
 *
 * see - https://ktor.io/docs/interactive-website-add-persistence.html#connect_db
 */
fun Application.configureDatabases() {
    val database = Database.connect(
        url = EnvConfig.getString("database.url"),
        user = EnvConfig.getString("database.user"),
        driver = EnvConfig.getString("database.driver"),
        password = EnvConfig.getString("database.password")
    )

    transaction(database) {
        SchemaUtils.create(UserTable.Users)
        SchemaUtils.create(ProfileTable.Profiles)
        SchemaUtils.create(FollowTable.Follows)
    }
}
