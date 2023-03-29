package ndy.routers

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.ktor.client.shouldHaveStatus
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.checkAll
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import ndy.test.generator.ProfileArbs.usernameValueArb
import ndy.test.generator.UserArbs.emailValueArb
import ndy.test.generator.UserArbs.passwordValueArb
import ndy.test.generator.registerArb
import ndy.test.spec.BaseSpec
import ndy.test.util.integrationTest
import ndy.test.util.registerUser

class UserRoutesTest : BaseSpec(body = {
    integrationTest("login") { client ->
        registerArb<RegistrationRequest>(usernameValueArb, emailValueArb, passwordValueArb)

        checkAll<RegistrationRequest> { registrationRequest ->
            registerUser(client, registrationRequest)

            // login with that user
            val response = client.post("/api/users/login") {
                contentType(ContentType.Application.Json)
                setBody(mapOf("user" to LoginRequest(registrationRequest.email, registrationRequest.password)))
            }

            response shouldHaveStatus HttpStatusCode.OK
            assertSoftly(response.body<Map<String, UserResponse>>()["user"]!!) {
                it.token shouldNotBe null
                it.email shouldBe registrationRequest.email
                it.username shouldBe registrationRequest.username
                it.bio shouldBe null
                it.image shouldBe null
            }
        }
    }

    integrationTest("login with no user") { client ->
        val response = client.post("/api/users/login") {
            contentType(ContentType.Application.Json)
            setBody("{\"no\":\"user\"}")
        }

        response shouldHaveStatus HttpStatusCode.BadRequest
        response.bodyAsText() shouldBe "" // TODO - https://ktor.io/docs/status-pages.html
    }

    integrationTest("signup") { client ->
        registerArb<RegistrationRequest>(usernameValueArb, emailValueArb, passwordValueArb)

        checkAll<RegistrationRequest> { request ->
            val response = client.post("/api/users") {
                contentType(ContentType.Application.Json)
                setBody(mapOf("user" to request))
            }

            response shouldHaveStatus HttpStatusCode.Created
            assertSoftly(response.body<Map<String, UserResponse>>()["user"]!!) {
                it.token shouldBe null
                it.email shouldBe request.email
                it.username shouldBe request.username
                it.bio shouldBe null
                it.image shouldBe null
            }
        }
    }

    integrationTest("signup with no user") { client ->
        val response = client.post("/api/users") {
            contentType(ContentType.Application.Json)
            setBody("{\"no\":\"user\"}")
        }

        response shouldHaveStatus HttpStatusCode.BadRequest
    }
})
