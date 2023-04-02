package ndy.test.util

import io.kotest.assertions.ktor.client.shouldHaveStatus
import io.kotest.property.assume
import io.ktor.client.call.*
import io.ktor.client.plugins.resources.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.HttpStatusCode.Companion.Created
import io.ktor.http.HttpStatusCode.Companion.OK
import ndy.api.dto.LoginRequest
import ndy.api.dto.RegistrationRequest
import ndy.api.dto.UserResponse
import ndy.api.resources.Profiles
import ndy.api.resources.Users
import ndy.plugins.TOKEN_SCHEMA
import ndy.test.spec.HttpClientContext

/**
 * Contains some helper functions that create/setup data for routeTests
 */

suspend inline fun <reified T : Any> HttpResponse.extract(key: String): T = body<Map<String, T>>()[key]!!

context (HttpClientContext)
suspend fun registerUser(request: RegistrationRequest) {
    assumeNotDuplicated(request.username)
    val response = client.post(Users()) {
        setBody(mapOf("user" to request))
    }

    response shouldHaveStatus Created
}

context (HttpClientContext)
suspend fun login(request: RegistrationRequest): String {
    return login(LoginRequest(request.email, request.password))
}

context (HttpClientContext)
suspend fun assumeNotDuplicated(username: String) {
    val response = client.post(Profiles.Username.Duplicated(parent = Profiles.Username(username = username)))

    response shouldHaveStatus OK
    val duplicated = response.extract<Boolean>("duplicated")
    assume(!duplicated)
}

context (HttpClientContext)
suspend fun login(request: LoginRequest): String {
    val response = client.post(Users.Login()) {
        contentType(ContentType.Application.Json)
        setBody(mapOf("user" to request))
    }
    response shouldHaveStatus OK
    return response.extract<UserResponse>("user").token!!
}

fun HttpMessageBuilder.authToken(token: String) {
    headers[HttpHeaders.Authorization] = "$TOKEN_SCHEMA $token"
}