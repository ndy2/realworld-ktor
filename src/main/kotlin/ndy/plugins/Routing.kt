package ndy.plugins

import io.ktor.server.application.*
import io.ktor.server.routing.*
import ndy.api.routers.articleRouting
import ndy.api.routers.profileRouting
import ndy.api.routers.tagRouting
import ndy.api.routers.userRouting
import ndy.global.context.applicationLoggingContext

/**
 * configure Routing (Controller in Spring)
 * *
 * see - https://ktor.io/docs/routing-in-ktor.html>routing-in-ktor in ktor docs
 */
fun Application.configureRouting() {

    routing {
        with(this@configureRouting.applicationLoggingContext()) {
            route("/api") {
                userRouting()
                profileRouting()
                articleRouting()
                tagRouting()
            }
        }
    }
}
