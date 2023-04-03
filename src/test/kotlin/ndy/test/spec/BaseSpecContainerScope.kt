package ndy.test.spec

import io.kotest.core.names.TestName
import io.kotest.core.spec.KotestTestScope
import io.kotest.core.spec.style.scopes.AbstractContainerScope
import io.kotest.core.test.TestScope

@KotestTestScope
class BaseSpecContainerScope(
    testScope: TestScope,
) : AbstractContainerScope(testScope) {

    /**
     * Adds a 'describe' container test as a child of the current test case.
     */
    suspend fun describe(name: String, test: suspend BaseSpecContainerScope.() -> Unit) {
        registerContainer(TestName(name), false, null) { BaseSpecContainerScope(this).test() }
    }

    /**
     * Adds a disabled container test to this container.
     */
    suspend fun xdescribe(name: String, test: suspend BaseSpecContainerScope.() -> Unit) {
        registerContainer(TestName(name), true, null) { BaseSpecContainerScope(this).test() }
    }

    /**
     * Adds a integration test case to this container.
     */
    suspend fun integrationTest(name: String, block: suspend context(HttpClientContext) () -> Unit) {
        registerTest(TestName(name), false, null, integrationTest(block))
    }

    /**
     * Adds a disabled integration test case to this container.
     */
    suspend fun xintegrationTest(name: String, block: suspend context(HttpClientContext) () -> Unit) {
        registerTest(TestName(name), true, null, integrationTest(block))
    }

    /**
     * Adds a transactional test case to this container.
     */
    suspend fun transactionalTest(name: String, block: suspend TestScope.() -> Unit) {
        registerTest(TestName(name), false, null, transactionalTest(block))
    }

    /**
     * Adds a disabled transactional test case to this container.
     */
    suspend fun xtransactionalTest(name: String, block: suspend TestScope.() -> Unit) {
        registerTest(TestName(name), true, null, transactionalTest(block))
    }
}
