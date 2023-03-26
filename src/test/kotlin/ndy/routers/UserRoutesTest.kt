package ndy.routers

import io.kotest.property.checkAll
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import junit.framework.TestCase.assertEquals
import ndy.test.generator.UserArbs.emailValueArb
import ndy.test.generator.UserArbs.passwordValueArb
import ndy.test.generator.UserArbs.usernameValueArb
import ndy.test.generator.registerArb

import ndy.test.spec.BaseSpec
import ndy.test.util.integrationTest

class UserRoutesTest : BaseSpec({
    integrationTest("authentication") { client ->
        client.post("/api/users/login").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("login request received!", bodyAsText())
        }
    }

    integrationTest("signup") { client ->
        registerArb<RegistrationRequest>(usernameValueArb, emailValueArb, passwordValueArb)

        checkAll<RegistrationRequest> { request ->
            client.post("/api/users") {
                contentType(ContentType.Application.Json)
                setBody(mapOf("user" to request))
            }
                .apply {
                    assertEquals(HttpStatusCode.Created, status)
                }
        }
    }
})
