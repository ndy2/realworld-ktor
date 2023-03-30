package ndy.routers

import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import ndy.domain.profile.application.ProfileService
import ndy.exception.UsernameDuplicatedException
import ndy.resources.Profiles
import ndy.util.ok
import org.koin.ktor.ext.inject

fun Route.profileRouting() {
    val profileService by inject<ProfileService>()

    post<Profiles.Username.Duplicated> {
        try {
            profileService.checkUsernameDuplicated(it.parent.username)
            call.ok(mapOf("duplicated" to false))
        } catch (e: UsernameDuplicatedException) {
            call.ok(mapOf("duplicated" to true))
        }
    }
}

