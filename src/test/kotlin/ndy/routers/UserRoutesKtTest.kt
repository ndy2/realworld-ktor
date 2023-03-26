package ndy.routers

import io.kotest.core.spec.style.FunSpec
import io.kotest.property.PropertyTesting
import io.kotest.property.arbitrary.ArbitraryBuilder
import io.kotest.property.checkAll
import io.kotest.property.resolution.GlobalArbResolver
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import junit.framework.TestCase.assertEquals
import ndy.test.generator.ArbNullEdgecase
import ndy.test.generator.UserArbs
import ndy.test.util.integrationTest

class UserRoutesKtTest : FunSpec({
    integrationTest("authentication") { client ->
        client.post("/api/users/login").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("login request received!", bodyAsText())
        }
    }

    integrationTest("signup") { client ->
        PropertyTesting.defaultIterationCount = 10

        GlobalArbResolver.register<RegistrationRequest>(
            object : ArbNullEdgecase<RegistrationRequest>(
                { rs ->
                    ArbitraryBuilder.create {
                        RegistrationRequest(
                            UserArbs.UsernameValueArb.sample(it).value,
                            UserArbs.EmailValueArb.sample(it).value,
                            UserArbs.PasswordValueArb.sample(it).value,
                        )
                    }.build().sample(rs)
                },
            ) {}
        )

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
