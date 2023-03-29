package ndy.resources

import io.ktor.resources.*

@Resource("/profiles")
class Profiles {

    @Resource("/{username}")
    class Username(val parent: Profiles = Profiles(), val username: String) {

        @Resource("/follow")
        class Follow(val parent: Username)
    }
}