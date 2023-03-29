package ndy.test.util

import io.kotest.assertions.ktor.client.shouldHaveStatus
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.HttpStatusCode.Companion.Created
import io.ktor.http.HttpStatusCode.Companion.OK
import ndy.routers.LoginRequest
import ndy.routers.RegistrationRequest
import ndy.routers.UserResponse

/**
 * Contains some helper functions that create/setup data for routeTests
 */

suspend inline fun <reified T : Any> HttpResponse.extract(key: String): T = body<Map<String, T>>()[key]!!

context (HttpClientContext)
suspend fun registerUser(request: RegistrationRequest) {
    val response = client.post("/api/users") {
        contentType(ContentType.Application.Json)
        setBody(mapOf("user" to request))
    }

    response shouldHaveStatus Created
}

context (HttpClientContext)
suspend fun login(request: LoginRequest): String {
    val response = client.post("/api/users/login") {
        contentType(ContentType.Application.Json)
        setBody(mapOf("user" to request))
    }

    response shouldHaveStatus OK
    return response.extract<UserResponse>("user").token!!
}