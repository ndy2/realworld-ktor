package ndy.test.spec

import io.ktor.client.*

interface HttpClientContext {
    val client: HttpClient
}