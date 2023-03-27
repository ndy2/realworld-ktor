package ndy.exception

class RealworldRuntimeException(
    message: String?,
    e: Exception?
) : RuntimeException(message, e)