package ndy.global.exception

class UsernameDuplicatedException(username: String) : RealworldRuntimeException("'$username' is already used")
