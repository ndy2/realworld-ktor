package ndy.plugins

import io.ktor.server.application.*
import io.ktor.server.routing.*
import ndy.global.context.DefaultLoggingContext
import ndy.api.routers.profileRouting
import ndy.api.routers.userRouting

/**
 * configure Routing (Controller in Spring)
 *
 * see - https://ktor.io/docs/routing-in-ktor.html>routing-in-ktor in ktor docs
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
