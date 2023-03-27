package ndy.plugins

import io.ktor.server.application.*
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
        url = environment.config.property("database.url").getString(),
        user = environment.config.property("database.user").getString(),
        driver = environment.config.property("database.driver").getString(),
        password = environment.config.property("database.password").getString()
    )
    transaction(database) {
        SchemaUtils.create(UserTable.Users)
    }
}
