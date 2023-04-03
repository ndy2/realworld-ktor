package ndy.api.resources

import io.ktor.resources.Resource

@Resource("/users")
class Users {

    @Resource("/login")
    class Login(val parent: Users = Users())
}
