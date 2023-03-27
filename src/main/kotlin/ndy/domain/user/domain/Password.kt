package ndy.domain.user.domain

class Password(
    value: String
) {
    val encodedPassword = "[encoded]$value"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Password
        if (encodedPassword != other.encodedPassword) return false
        return true
    }

    override fun hashCode(): Int {
        return encodedPassword.hashCode()
    }

    override fun toString(): String {
        return "Password(encodedPassword='$encodedPassword')"
    }
}
