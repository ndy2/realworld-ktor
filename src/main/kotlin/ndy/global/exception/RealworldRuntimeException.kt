package ndy.global.exception

/**
 * Root RuntimeException for Realworld Application
 */
abstract class RealworldRuntimeException(
    message: String? = null,
    e: Exception? = null
) : RuntimeException(message, e)
