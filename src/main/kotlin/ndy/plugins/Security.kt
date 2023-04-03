package ndy.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import de.sharpmind.ktor.EnvConfig
import io.ktor.server.application.Application
import io.ktor.server.auth.authentication
import io.ktor.server.auth.jwt.jwt
import ndy.domain.profile.domain.ProfileId
import ndy.domain.user.domain.UserId
import ndy.global.exception.AuthenticationException
import ndy.global.security.Principal

const val TOKEN_SCHEMA = "Token"

/**
 * configure jwt in ktor
 *
 * reference - https://ktor.io/docs/jwt.htm
 */
fun Application.configureSecurity() {
    authentication {
        jwt {
            authSchemes(TOKEN_SCHEMA /* default : "Bearer" */)

            realm = EnvConfig.getString("jwt.realm")
            verifier(
                JWT
                    .require(Algorithm.HMAC256(EnvConfig.getString("jwt.secret")))
                    .withAudience(EnvConfig.getString("jwt.audience"))
                    .withIssuer(EnvConfig.getString("jwt.issuer"))
                    .build()
            )

            validate {
                val userIdClaim = it.payload.getClaim("user_id") ?: throw AuthenticationException("login failure")
                val profileIdClaim = it.payload.getClaim("profile_id") ?: throw AuthenticationException("login failure")

                val userId = UserId(userIdClaim.asLong().toULong())
                val profileId = ProfileId(profileIdClaim.asLong().toULong())

                Principal(userId, profileId)
            }
        }
    }
}
