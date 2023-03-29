package ndy.resources

import io.ktor.resources.*

@Resource("/users")
class Users {
    @Resource("/login")
    class Login(val parent: Users = Users())
}

@Resource("/user")
class User