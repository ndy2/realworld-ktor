package ndy.domain.user.application

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import de.sharpmind.ktor.EnvConfig
import ndy.domain.user.domain.User
import java.util.*

object JwtTokenService {

    fun createToken(user: User): String = JWT.create()
        .withAudience(EnvConfig.getString("jwt.audience"))
        .withIssuer(EnvConfig.getString("jwt.issuer"))
        .withClaim("id", user.id.value.toInt())
        .withExpiresAt(Date(System.currentTimeMillis() + EnvConfig.getInt("jwt.expires")))
        .sign(Algorithm.HMAC256(EnvConfig.getString("jwt.secret")))
}