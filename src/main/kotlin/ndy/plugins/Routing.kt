package ndy.plugins

import io.ktor.server.application.*
import io.ktor.server.routing.*
import ndy.context.DefaultLoggingContext
import ndy.routers.profileRouting
import ndy.routers.userRouting

/**
 * configure Routing (Controller in Spring)
 * @see - <a href=https://ktor.io/docs/routing-in-ktor.html>routing-in-ktor in ktor docs</a>
 */
fun Application.configureRouting() {

    routing {
        with(DefaultLoggingContext) {
            route("/api") {
                userRouting()
                profileRouting()
            }
        }
    }
}
