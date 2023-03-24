package ndy

import io.ktor.server.application.*
import ndy.plugins.*
import io.ktor.server.netty.EngineMain.main as netty

fun main(args: Array<String>) = netty(args)

@Suppress("unused")
fun Application.module() {
    configureDi()
    configureSecurity()
    configureHTTP()
    configureSerialization()
    configureDatabases()
    configureRouting()
}
