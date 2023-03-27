package ndy.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

/**
 * configure jwt in ktor
 *
 * see https://ktor.io/docs/jwt.htm
 */
fun Application.configureSecurity() {

    authentication {
        jwt {
            val jwtAudience = this@configureSecurity.readProperty("jwt.audience")
            realm = this@configureSecurity.readProperty("jwt.realm")
            verifier(
                JWT
                    .require(Algorithm.HMAC256(this@configureSecurity.readProperty("jwt.secret")))
                    .withAudience(jwtAudience)
                    .withIssuer(this@configureSecurity.readProperty("jwt.domain"))
                    .build()
            )
        }
    }
}

private fun Application.readProperty(path: String) =
    this.environment.config.property(path).getString()
