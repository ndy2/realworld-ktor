package ndy.exception

class UsernameDuplicatedException(username: String) : RealworldRuntimeException("'$username' is already used")