package ndy.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.resources.Resources

/**
 * configure resources with ktor-server-resources plugin
 *
 * reference - https://ktor.io/docs/type-safe-routing.html
 */
fun Application.configureResources() {
    install(Resources)
}
