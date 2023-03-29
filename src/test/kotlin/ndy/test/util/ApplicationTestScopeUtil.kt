package ndy.test.util

import io.kotest.core.spec.style.FunSpec
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import org.koin.core.context.stopKoin

fun FunSpec.xintegrationTest(name: String, block: suspend ApplicationTestBuilder.(client: HttpClient) -> Unit) =
    xtest(name) {
        stopKoin()
        testApplication {
            val client = createClient {
                install(ContentNegotiation) { json() }
            }
            block(this, client)
        }
    }


fun FunSpec.integrationTest(name: String, block: suspend context(ClientContext) () -> Unit) =
    test(name) {
        stopKoin()
        testApplication {
            val client = createClient {
                install(ContentNegotiation) { json() }
            }

            val clientContext = object : ClientContext {
                override val client = client
            }

            block(clientContext)
        }
    }

interface ClientContext {
    val client: HttpClient
}

