package ndy

import io.ktor.server.application.*
import ndy.plugins.*
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
    configureRouting()
}
