package ndy.domain.user.domain

import ndy.exception.AuthenticationException
import ndy.util.checkValidation
import kotlin.reflect.KProperty

const val MAX_USER_PASSWORD_LENGTH = 32

/**
 * Password - 비밀번호
 */
/* default constructor with rawPassword & encoder - typically used in user registration process */
class Password(
    rawPassword: String? = null,
    passwordEncoder: PasswordEncoder? = null,
) {
    init {
        rawPassword?.let { checkValidation(it.length <= MAX_USER_PASSWORD_LENGTH, "password too long") }
    }

    var encodedPassword: String by PasswordDelegate(rawPassword, passwordEncoder)

    override fun equals(other: Any?) =
        if (this === other) true
        else if (javaClass != other?.javaClass) false
        else encodedPassword == (other as Password).encodedPassword

    override fun hashCode() = encodedPassword.hashCode()
    override fun toString() = "Password(encodedPassword='[ENCRYPTED]')"

    companion object {
        /* create Password with encodedPassword - typically used for authentication process  */
        fun withEncoded(encodedPassword: String): Password {
            val password = Password()
            password.encodedPassword = encodedPassword
            return password
        }
    }

    /* throw exception if password verification failed! */
    fun checkPassword(attemptPassword: String, passwordVerifier: PasswordVerifier) {
        if (!passwordVerifier.verify(attemptPassword, encodedPassword)) {
            throw AuthenticationException("login failure")
        }
    }
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