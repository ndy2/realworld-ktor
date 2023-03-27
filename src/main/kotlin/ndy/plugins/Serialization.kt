package ndy.plugins

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*

/**
 * configure ContentNegotiation with json-kotlinx
 *
 * see https://ktor.io/docs/serialization.html
 */
fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json()
    }
}
