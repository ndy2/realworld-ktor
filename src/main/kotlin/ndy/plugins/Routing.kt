package ndy.plugins

import io.ktor.server.application.*
import io.ktor.server.routing.*
import ndy.routers.userRouting

/**
 * configure Routing (Controller in Spring)
 * @see - <a href=https://ktor.io/docs/routing-in-ktor.html>routing-in-ktor in ktor docs</a>
 */
fun Application.configureRouting() {

    routing {
        route("/api") {
            userRouting()
        }
    }
}
