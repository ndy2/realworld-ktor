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


// TestScope/ Context 와 관련된 기능은 포기
// block: suspend context(HttpClientContext) TestScope.() -> Unit
fun FunSpec.integrationTest(name: String, block: suspend context(HttpClientContext) () -> Unit) =
    test(name) {
        stopKoin()
        testApplication {
            val client = createClient {
                install(ContentNegotiation) { json() }
            }

            val clientContext = object : HttpClientContext {
                override val client: HttpClient = client
            }

            block(clientContext)
        }
    }

interface HttpClientContext {
    val client: HttpClient
}