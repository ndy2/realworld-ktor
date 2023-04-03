package ndy.domain.user.application

import org.mindrot.jbcrypt.BCrypt
import ndy.domain.user.domain.PasswordEncoder
import ndy.domain.user.domain.PasswordVerifier

object BcryptPasswordService : PasswordEncoder, PasswordVerifier {

    override fun encode(rawPassword: String): String {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt())
    }

    override fun verify(rawPassword: String, encodedPassword: String): Boolean {
        return BCrypt.checkpw(rawPassword, encodedPassword)
    }
}
