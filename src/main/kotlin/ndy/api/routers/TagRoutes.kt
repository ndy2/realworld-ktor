package ndy.api.routers

import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.routing.*
import ndy.api.resources.Tags
import ndy.domain.tag.application.TagService
import ndy.global.util.ok
import org.koin.ktor.ext.inject

fun Route.tagRouting() {

    val service by inject<TagService>()

    get<Tags> {
        // action
        service.getAll()

        // response
        call.ok("tag list - TODO")
    }
}
