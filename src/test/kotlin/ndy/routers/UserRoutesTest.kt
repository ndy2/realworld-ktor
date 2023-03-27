package ndy.routers

import io.kotest.property.checkAll
import io.ktor.client.request.*
import io.ktor.http.*
import junit.framework.TestCase.assertEquals
import ndy.test.generator.UserArbs.emailValueArb
import ndy.test.generator.UserArbs.passwordValueArb
import ndy.test.generator.UserArbs.usernameValueArb
import ndy.test.generator.registerArb
import ndy.test.spec.BaseSpec
import ndy.test.util.integrationTest
import ndy.test.util.xintegrationTest

class UserRoutesTest : BaseSpec(body = {
    xintegrationTest("authentication") { client ->
        registerArb<LoginRequest>(emailValueArb, passwordValueArb)

        checkAll<LoginRequest> { request ->
            client.post("/api/users/login") {
                contentType(ContentType.Application.Json)
                setBody(mapOf("user" to request))
            }.apply {
                assertEquals(HttpStatusCode.OK, status)
            }
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
