package ndy.test.util

import io.kotest.assertions.ktor.client.shouldHaveStatus
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import ndy.routers.RegistrationRequest

/**
 * Contains some helper functions that create/setup data for routeTests
 */

suspend fun registerUser(client: HttpClient, request: RegistrationRequest) {
    val response = client.post("/api/users") {
        contentType(ContentType.Application.Json)
        setBody(mapOf("user" to request))
    }

    response shouldHaveStatus HttpStatusCode.Created
}

