package ndy.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import de.sharpmind.ktor.EnvConfig
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import ndy.domain.user.domain.UserId
import ndy.exception.AuthenticationException

/**
 * configure jwt in ktor
 *
 * see https://ktor.io/docs/jwt.htm
 */
fun Application.configureSecurity() {

    authentication {
        jwt {
            val jwtAudience = EnvConfig.getString("jwt.audience")
            realm = EnvConfig.getString("jwt.realm")
            verifier(
                JWT
                    .require(Algorithm.HMAC256(EnvConfig.getString("jwt.secret")))
                    .withAudience(jwtAudience)
                    .withIssuer(EnvConfig.getString("jwt.issuer"))
                    .build()
            )

            validate {
                val claim = it.payload.getClaim("id") ?: throw AuthenticationException("login failure")
                UserId(claim.asLong().toULong())
            }
        }
    }
}

private fun Application.readProperty(path: String) =
    this.environment.config.property(path).getString()
