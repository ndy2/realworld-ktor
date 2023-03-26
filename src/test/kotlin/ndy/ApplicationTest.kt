package ndy

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals
import io.ktor.client.plugins.contentnegotiation.*
class ApplicationTest {

    @Test
    fun testRoot() = testApplication {
        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("Hello World!", bodyAsText())
        }
    }

    @Test
    fun testPostRoot() = testApplication {
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        client.post("/") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("hello" to "world!"))
        }.apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("haha : world!", bodyAsText())
        }
    }
}
