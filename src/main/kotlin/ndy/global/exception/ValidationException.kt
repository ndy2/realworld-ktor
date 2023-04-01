package ndy.global.exception

class ValidationException(message: String?, e: Exception? = null) : RealworldRuntimeException(message, e)