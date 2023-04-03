package ndy.domain.user.application

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import de.sharpmind.ktor.EnvConfig
import ndy.domain.profile.domain.Profile
import ndy.domain.user.domain.User

import java.util.Date

object JwtTokenService {

    fun createToken(user: User, profile: Profile): String = JWT.create()
        .withAudience(EnvConfig.getString("jwt.audience"))
        .withIssuer(EnvConfig.getString("jwt.issuer"))
        .withClaim("user_id", user.id.value.toLong())
        .withClaim("profile_id", profile.id.value.toLong())
        .withExpiresAt(Date(System.currentTimeMillis() + EnvConfig.getInt("jwt.expires")))
        .sign(Algorithm.HMAC256(EnvConfig.getString("jwt.secret")))
}
