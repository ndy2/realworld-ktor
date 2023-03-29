package ndy.exception

abstract class RealworldRuntimeException(
    message: String? = null,
    e: Exception? = null
) : RuntimeException(message, e)