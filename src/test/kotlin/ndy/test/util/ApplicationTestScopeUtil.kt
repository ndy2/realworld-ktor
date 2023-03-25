package ndy.test.util

import io.kotest.core.spec.style.FunSpec
import io.ktor.server.testing.*

fun FunSpec.integrationTest(name: String, block: suspend ApplicationTestBuilder.() -> Unit) =
    test(name) {
        testApplication(block)
    }
