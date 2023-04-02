package ndy.test.util

import io.kotest.core.spec.style.FunSpec
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.resources.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import org.koin.core.context.stopKoin

fun FunSpec.xintegrationTest(name: String, block: suspend context(HttpClientContext) () -> Unit) = xtest(name) {}

// TestScope/ Context 와 관련된 기능은 포기...
// block: suspend context(HttpClientContext) TestScope.() -> Unit
// related issue - https://youtrack.jetbrains.com/issue/KT-52213/Context-receivers-No-mapping-for-symbol-VALUEPARAMETER-caused-by-contextual-suspending-function-type-with-receiver
fun FunSpec.integrationTest(name: String, block: suspend context(HttpClientContext) () -> Unit) =
    test(name) {
        stopKoin()
        testApplication {
            val client = createClient {
                install(DefaultRequest) {
                    url {
                        path("api/")
                    }
                    contentType(ContentType.Application.Json)
                }

                install(ContentNegotiation) {
                    json(Json {
                        explicitNulls = false
                    })
                }
                install(Resources)
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