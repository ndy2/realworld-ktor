package ndy.plugins

import de.sharpmind.ktor.EnvConfig
import io.ktor.server.application.*

/**
 * Configure environment property available from everywhere.
 * We can mimic spring's `Environment` with this plugin!
 *
 * reference - https://github.com/sharpmind-de/ktor-env-config
 */
fun Application.configureProperties() {
    EnvConfig.initConfig(environment.config)
}
