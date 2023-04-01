package ndy.plugins

import io.ktor.server.application.*
import io.ktor.server.resources.*

/**
 * configure resources with ktor-server-resources plugin
 * *
 * see - https://ktor.io/docs/type-safe-routing.html
 */
fun Application.configureResources() {
    install(Resources)
}
