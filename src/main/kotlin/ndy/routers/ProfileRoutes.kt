package ndy.routers

import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import ndy.domain.profile.application.ProfileService
import ndy.util.putWithAuthenticatedUser
import org.koin.ktor.ext.inject

fun Route.profileRouting() {

    val profileService by inject<ProfileService>()

    route("/profile") {
        authenticate {

            // profile update
            putWithAuthenticatedUser(OK) {
                "profile update request received"
            }
        }
    }
}
