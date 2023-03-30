package ndy.routers

import io.ktor.server.application.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import ndy.domain.profile.application.ProfileResult
import ndy.domain.profile.application.ProfileService
import ndy.resources.Profiles
import ndy.util.authenticatedDelete
import ndy.util.authenticatedGet
import ndy.util.authenticatedPost
import ndy.util.ok
import org.koin.ktor.ext.inject

fun Route.profileRouting() {

    val service by inject<ProfileService>()

    authenticatedGet<Profiles.Username>(optional = true) {
        // bind
        val username = it.username

        // action
        val result = service.getByUsername(username)

        // return
        val response = ProfileResponse.ofResult(result)
        call.okProfile(response)
    }

    authenticatedPost<Profiles.Username.Follow> {
        // bind
        val username = it.parent.username

        // action
        val result = service.follow(username)

        // return
        val response = ProfileResponse.ofResult(result)
        call.okProfile(response)
    }

    authenticatedDelete<Profiles.Username.Follow> {
        // bind
        val username = it.parent.username

        // action
        val result = service.unfollow(username)

        // return
        val response = ProfileResponse.ofResult(result)
        call.okProfile(response)
    }

    post<Profiles.Username.Duplicated> {
        // bind
        val username = it.parent.username

        // action
        val duplicated = service.checkUsernameDuplicated(username)

        // return
        call.ok(mapOf("duplicated" to duplicated))
    }
}

private suspend inline fun <reified T> ApplicationCall.okProfile(response: T) {
    ok(mapOf("profile" to response))
}

@Serializable
data class ProfileResponse(
    val username: String,
    val bio: String?,
    val image: String?,
    val following: Boolean,
) {
    companion object {
        fun ofResult(result: ProfileResult) = ProfileResponse(
            username = result.username,
            bio = result.bio,
            image = result.image,
            following = result.following
        )
    }
}