package ndy.test.spec

import io.kotest.core.names.TestName
import io.kotest.core.spec.RootTest
import io.kotest.core.spec.style.scopes.RootScope
import io.kotest.core.spec.style.scopes.addContainer
import io.kotest.core.spec.style.scopes.addTest
import io.kotest.core.test.TestScope


/**
 * supports dsl-methods for the 'BaseSpec' style.
 * -  add integrationTest feature
 * -  add transactionalTest feature
 * -  use `describe` as container name to avoid overloading issue
 */
interface BaseSpecRootScope : RootScope {

    /**
     * Adds a container [RootTest] that uses a [BaseSpecContainerScope] as the test container.
     */
    fun describe(name: String, test: suspend BaseSpecContainerScope.() -> Unit) {
        addContainer(TestName("describe ", name, false), false, null) { BaseSpecContainerScope(this).test() }
    }

    /**
     * Adds a disabled container [RootTest] that uses a [BaseSpecContainerScope] as the test container.
     */
    fun xdescribe(name: String, test: suspend BaseSpecContainerScope.() -> Unit) =
        addContainer(TestName("describe ", name, false), true, null) { BaseSpecContainerScope(this).test() }


    /**
     * Adds a Root IntegrationTest, with the given name and default config.
     */
    fun integrationTest(name: String, block: suspend context(HttpClientContext) () -> Unit) {
        addTest(TestName(name), false, null, integrationTest(block))
    }

    /**
     * Adds a Root IntegrationTest, with the given name and default config.
     */
    fun xintegrationTest(name: String, block: suspend context(HttpClientContext) () -> Unit) {
        addTest(TestName(name), true, null, integrationTest(block))
    }

    /**
     * Adds a Root transactionalTest, with the given name and default config.
     */
    fun transactionalTest(name: String, block: suspend TestScope.() -> Unit) {
        addTest(TestName(name), false, null, transactionalTest(block))
    }

    /**
     * Adds a Root transactionalTest, with the given name and default config.
     */
    suspend fun xtransactionalTest(name: String, block: suspend TestScope.() -> Unit) {
        addTest(TestName(name), true, null, transactionalTest(block))
    }
}
