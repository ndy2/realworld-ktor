package ndy.api.routers

import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.resources.post
import io.ktor.server.routing.Route
import ndy.api.dto.ProfileResponse
import ndy.api.resources.Profiles
import ndy.domain.profile.application.ProfileService
import ndy.global.util.authenticatedDelete
import ndy.global.util.authenticatedGet
import ndy.global.util.authenticatedPost
import ndy.global.util.ok
import org.koin.ktor.ext.inject

fun Route.profileRouting() {
    val service by inject<ProfileService>()

    /**
     * Get Profile
     * GET /api/profiles/{username}
     */
    authenticatedGet<Profiles.Username>(optional = true) {
        // bind
        val username = it.username

        // action
        val result = service.getByUsername(username)

        // return
        val response = ProfileResponse.ofResult(result)
        call.okProfile(response)
    }

    /**
     * Follow User
     * POST /api/profiles/{username}/follow
     */
    authenticatedPost<Profiles.Username.Follow> {
        // bind
        val username = it.parent.username

        // action
        val result = service.follow(username)

        // return
        val response = ProfileResponse.ofResult(result)
        call.okProfile(response)
    }

    /**
     * Unfollow User
     * DELETE /api/profiles/{username}/follow
     */
    authenticatedDelete<Profiles.Username.Follow> {
        // bind
        val username = it.parent.username

        // action
        val result = service.unfollow(username)

        // return
        val response = ProfileResponse.ofResult(result)
        call.okProfile(response)
    }

    /**
     * Check Username Duplicated
     * POST /api/profiles/{username}/duplicated
     * *
     * for convenience in test (it's more like real scenario)
     */
    post<Profiles.Username.Duplicated> {
        // bind
        val username = it.parent.username

        // action
        val duplicated = service.checkUsernameDuplicated(username)

        // return
        call.ok(mapOf("duplicated" to duplicated))
    }
}

private suspend inline fun ApplicationCall.okProfile(response: ProfileResponse) {
    ok(mapOf("profile" to response))
}
