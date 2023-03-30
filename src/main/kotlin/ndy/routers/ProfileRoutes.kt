package ndy.routers

import io.ktor.server.application.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import ndy.domain.profile.application.ProfileService
import ndy.resources.Profiles
import ndy.util.authenticatedGet
import ndy.util.ok
import org.koin.ktor.ext.inject

fun Route.profileRouting() {

    val profileService by inject<ProfileService>()

    authenticatedGet<Profiles.Username>(optional = true) {
        // bind
        val username = it.username

    }

    post<Profiles.Username.Duplicated> {
        // bind
        val username = it.parent.username

        // action
        val duplicated = profileService.checkUsernameDuplicated(username)

        // return
        call.ok(mapOf("duplicated" to duplicated))
    }
}

