package ndy.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import de.sharpmind.ktor.EnvConfig
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import ndy.domain.user.domain.UserId
import ndy.global.exception.AuthenticationException

const val TOKEN_SCHEMA = "Token"

/**
 * configure jwt in ktor
 *
 * see https://ktor.io/docs/jwt.htm
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
                val claim = it.payload.getClaim("id") ?: throw AuthenticationException("login failure")
                UserId(claim.asLong().toULong())
            }
        }
    }
}
