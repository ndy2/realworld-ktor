package ndy.exception

open class RealworldRuntimeException(
    message: String? = null,
    e: Exception? = null
) : RuntimeException(message, e)