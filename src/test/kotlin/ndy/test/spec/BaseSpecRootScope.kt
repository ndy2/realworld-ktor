package ndy.test.spec

import io.kotest.core.spec.style.scopes.FunSpecRootScope
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.resources.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import org.koin.core.context.stopKoin


/**
 * Extends FunSpec with dsl-methods for the 'BaseSpec spec' style.
 * *
 * - 1. add integrationTest feature
 * - 2. add transactionalTest feature
 */
interface BaseSpecRootScope : FunSpecRootScope {

    /**
     * Adds a Root IntegrationTest, with the given name and default config.
     */
    fun integrationTest(name: String, block: suspend context(HttpClientContext) () -> Unit) =
        test(name) {
            stopKoin()
            testApplication {
                val client = createClient {
                    install(DefaultRequest) {
                        url {
                            path("api/")
                        }
                        contentType(ContentType.Application.Json)
                    }

                    install(ContentNegotiation) {
                        json(Json {
                            explicitNulls = false
                        })
                    }
                    install(Resources)
                }

                val clientContext = object : HttpClientContext {
                    override val client: HttpClient = client
                }

                block(clientContext)
            }
        }

    /**
     * Adds a Disabled Root IntegrationTest, with the given name and default config.
     */
    fun xintegrationTest(name: String, block: suspend context(HttpClientContext) () -> Unit) = xtest(name) {}


}