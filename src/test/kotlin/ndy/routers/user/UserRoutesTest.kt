package ndy.routers.user

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.ktor.client.shouldHaveStatus
import io.kotest.core.listeners.BeforeSpecListener
import io.kotest.core.spec.Spec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.checkAll
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.http.HttpStatusCode.Companion.Created
import io.ktor.http.HttpStatusCode.Companion.OK
import ndy.test.generator.ProfileArbs.usernameValueArb
import ndy.test.generator.UserArbs.emailValueArb
import ndy.test.generator.UserArbs.passwordValueArb
import ndy.test.generator.registerArb
import ndy.test.spec.BaseSpec
import ndy.test.util.integrationTest
import ndy.test.util.registerUser

class UserRoutesTest : BaseSpec(RequestArb, body = {

    integrationTest("login") {
        checkAll<RegistrationRequest> { request ->
            registerUser(client, request)
            val response = client.post("/api/users/login") {
                contentType(ContentType.Application.Json)
                setBody(mapOf("user" to LoginRequest(request.email, request.password)))
            }

            response shouldHaveStatus OK
            assertSoftly(response.body<Map<String, UserResponse>>()["user"]!!) {
                it.token shouldNotBe null
                it.email shouldBe request.email
                it.username shouldBe request.username
                it.bio shouldBe null
                it.image shouldBe null
            }
        }
    }

    integrationTest("signup") {
        checkAll<RegistrationRequest> { request ->
            val response = client.post("/api/users") {
                contentType(ContentType.Application.Json)
                setBody(mapOf("user" to request))
            }

            response shouldHaveStatus Created
            assertSoftly(response.body<Map<String, UserResponse>>()["user"]!!) {
                it.token shouldBe null
                it.email shouldBe request.email
                it.username shouldBe request.username
                it.bio shouldBe null
                it.image shouldBe null
            }
        }
    }
})

object RequestArb : BeforeSpecListener {
    override suspend fun beforeSpec(spec: Spec) {
        registerArb<RegistrationRequest>(usernameValueArb, emailValueArb, passwordValueArb)
    }
}