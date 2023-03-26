package ndy.test.util

import io.kotest.core.spec.style.FunSpec
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*

fun FunSpec.integrationTest(name: String, block: suspend ApplicationTestBuilder.(client: HttpClient) -> Unit) =
    test(name) {
        testApplication {
            val client = createClient {
                install(ContentNegotiation) { json() }
            }
            block(this, client)
        }
    }

