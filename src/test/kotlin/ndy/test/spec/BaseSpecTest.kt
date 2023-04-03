package ndy.test.spec

import io.kotest.core.test.TestScope
import io.ktor.client.HttpClient
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.resources.Resources
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.path
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.testing.testApplication
import ndy.global.util.transactional

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

/**
 * BaseSpec Style Integration Test
 */
@OptIn(ExperimentalSerializationApi::class)
fun integrationTest(test: suspend context(HttpClientContext) () -> Unit): suspend TestScope.() -> Unit = {
    testApplication {
        // creat client with below configurations
        val client = createClient {
            // configure default request
            install(DefaultRequest) {
                url { path("api/") }
                contentType(ContentType.Application.Json)
            }

            // configure content negotiation
            install(ContentNegotiation) { json(Json { explicitNulls = false }) }

            // install ktor.client.Resources for type safe routing
            install(Resources)
        }

        // run test in the context of configured client
        val clientContext = object : HttpClientContext {
            override val client: HttpClient = client
        }
        test(clientContext)
    }
}

/**
 * a context of http client
 * can replace typical boilerplate code like testApplication -> createClient
 */
interface HttpClientContext {
    val client: HttpClient
}

/**
 * BaseSpec Style Transactional Test
 */
fun transactionalTest(test: suspend TestScope.() -> Unit): suspend TestScope.() -> Unit = {
    // wrap with transactional
    transactional {
        test()
    }
}
