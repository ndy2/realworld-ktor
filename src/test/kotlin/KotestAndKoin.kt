import ndy.domain.user.domain.Email
import ndy.domain.user.domain.Password
import ndy.domain.user.domain.UserRepository
import ndy.domain.user.domain.Username
import ndy.test.extentions.DB
import ndy.test.extentions.DI
import ndy.test.spec.BaseSpec
import org.koin.test.inject

class KotestAndKoin : BaseSpec(DB, DI) {

    private val userRepository by inject<UserRepository>()

    init {
        test("use userService") {
            userRepository.save(
                Username("12"),
                Email("haha"),
                Password("hahah")
            )
        }
    }
}