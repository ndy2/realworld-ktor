package ndy.api.routers

import io.ktor.server.application.call
import io.ktor.server.resources.get
import io.ktor.server.routing.Route
import org.koin.ktor.ext.inject
import ndy.api.resources.Tags
import ndy.domain.tag.application.TagResult
import ndy.domain.tag.application.TagService
import ndy.global.util.ok

fun Route.tagRouting() {
    val service by inject<TagService>()

    /**
     * Get Tags
     * GET /api/tags
     */
    get<Tags> {
        // action
        val resultList = service.getAll()

        // response
        val responseList = resultList.map(TagResult::name)
        call.ok(mapOf("tags" to responseList))
    }
}
