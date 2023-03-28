package ndy.exception

class RealworldRuntimeException(
    message: String? = null,
    e: Exception? = null
) : RuntimeException(message, e)