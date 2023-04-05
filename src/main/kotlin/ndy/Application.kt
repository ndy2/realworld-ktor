package ndy

import io.ktor.server.application.*
import ndy.plugins.configureDatabases
import ndy.plugins.configureDi
import ndy.plugins.configureExceptionHandling
import ndy.plugins.configureHTTP
import ndy.plugins.configureProperties
import ndy.plugins.configureResources
import ndy.plugins.configureRouting
import ndy.plugins.configureSecurity
import ndy.plugins.configureSerialization
import io.ktor.server.netty.EngineMain.main as netty

fun main(args: Array<String>) = netty(args)

@Suppress("unused")
fun Application.module() {
    configureDi()
    configureProperties()
    configureSecurity()
    configureHTTP()
    configureSerialization()
    configureDatabases()
    configureResources()
    configureExceptionHandling()
    configureRouting()
}
