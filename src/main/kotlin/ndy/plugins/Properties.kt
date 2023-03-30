package ndy.plugins

import de.sharpmind.ktor.EnvConfig
import io.ktor.server.application.*

/**
 * configure environment property available from everywhere
 * we can mimic spring's `Environment` with this plugin!
 *
 * see https://github.com/sharpmind-de/ktor-env-config
 */
fun Application.configureProperties() {
    EnvConfig.initConfig(environment.config)
}
