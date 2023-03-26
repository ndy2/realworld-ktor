package ndy.plugins

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ndy.domain.user.application.UserService
import ndy.routers.userRouting
import org.koin.ktor.ext.inject

fun Application.configureRouting() {

    routing {
        get("/") {
            call.respondText("Hello World!")
        }

        post("/") {
            val map = call.receive<Map<String, String>>()
            call.respondText("haha : ${map["hello"]}")
        }

        route("/api") {
            userRouting()
        }
    }
}
