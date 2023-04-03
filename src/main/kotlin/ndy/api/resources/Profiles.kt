package ndy.api.resources

import io.ktor.resources.Resource

@Resource("/profiles")
class Profiles {

    @Resource("/{username}")
    class Username(val parent: Profiles = Profiles(), val username: String) {

        @Resource("/follow")
        class Follow(val parent: Username)

        @Resource("/duplicated")
        class Duplicated(val parent: Username)
    }
}
