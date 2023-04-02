package ndy.test.util

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestScope
import ndy.global.util.transactional

fun FunSpec.transactionalTest(name: String, block: suspend TestScope.() -> Unit) {
    test(name) {
        transactional {
            block()
        }
    }
}
