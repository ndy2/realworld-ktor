package ndy.domain.user.domain

import ndy.global.exception.AuthenticationException
import ndy.global.util.checkValidation
import kotlin.reflect.KProperty

/**
 * Password - 비밀번호
 */
/* default constructor with rawPassword & encoder - typically used in user registration process */
class Password(
    rawPassword: String? = null,
    passwordEncoder: PasswordEncoder? = null,
) {
    companion object {
        const val MAX_LENGTH = 32

        /* create Password with encodedPassword - typically used for authentication process  */
        fun withEncoded(encodedPassword: String): Password {
            val password = Password()
            password.encodedPassword = encodedPassword
            return password
        }
    }

    init {
        rawPassword?.let { checkValidation(it.length <= MAX_LENGTH, "password too long") }
    }

    var encodedPassword: String by PasswordDelegate(rawPassword, passwordEncoder)

    /* throw exception if password verification failed! */
    // TODO - move throw logic to service - just check matches @domain
    fun checkPassword(attemptPassword: String, passwordVerifier: PasswordVerifier) {
        if (!passwordVerifier.verify(attemptPassword, encodedPassword)) {
            throw AuthenticationException("login failure")
        }
    }

    override fun equals(other: Any?) =
        if (this === other) true
        else if (javaClass != other?.javaClass) false
        else encodedPassword == (other as Password).encodedPassword

    override fun hashCode() = encodedPassword.hashCode()
    override fun toString() = "Password(encodedPassword='[ENCRYPTED]')"
}

class PasswordDelegate(private val raw: String?, private val encoder: PasswordEncoder?) {

    private var initialized = false
    private var encodedPassword: String? = null

    operator fun getValue(thisRef: Password, prop: KProperty<*>): String {
        return if (!initialized)
            if (this.encoder != null && this.raw != null) {
                val encodedPassword = this.encoder.encode(this.raw)
                setValue(thisRef, prop, encodedPassword)
                encodedPassword
            } else "illegal approach to get encodedPassword"
        else encodedPassword!!
    }

    operator fun setValue(password: Password, property: KProperty<*>, value: String) {
        this.encodedPassword = value
        initialized = true
    }
}

interface PasswordEncoder {
    fun encode(rawPassword: String): String
}

interface PasswordVerifier {
    fun verify(rawPassword: String, encodedPassword: String): Boolean
}