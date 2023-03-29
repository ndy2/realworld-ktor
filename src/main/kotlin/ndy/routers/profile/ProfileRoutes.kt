package ndy.routers.profile

import io.ktor.server.routing.*
import ndy.domain.profile.application.ProfileService
import org.koin.ktor.ext.inject

fun Route.profileRouting() {

    val profileService by inject<ProfileService>()


}
