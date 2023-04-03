package ndy.plugins

import io.ktor.server.application.Application
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import ndy.api.routers.articleRouting
import ndy.api.routers.profileRouting
import ndy.api.routers.tagRouting
import ndy.api.routers.userRouting
import ndy.global.context.applicationLoggingContext

/**
 * configure Routing (Controller in Spring)
 *
 * reference - https://ktor.io/docs/routing-in-ktor.html>routing-in-ktor in ktor docs
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
