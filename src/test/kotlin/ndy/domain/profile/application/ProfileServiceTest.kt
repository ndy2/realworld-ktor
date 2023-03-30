package ndy.domain.profile.application

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import io.kotest.property.checkAll
import ndy.context.userIdContext
import ndy.infra.tables.ProfileTable
import ndy.test.extentions.DB
import ndy.test.extentions.DI
import ndy.test.extentions.JWT
import ndy.test.generator.ProfileArbs.userIdArb
import ndy.test.generator.ProfileArbs.usernameValueArb
import ndy.test.spec.BaseSpec
import ndy.test.util.assumeNotDuplicated
import ndy.util.newTransaction
import org.koin.test.inject

class ProfileServiceTest : BaseSpec(DI, DB, JWT) {

    private val sut: ProfileService by inject()

    init {
        with(ProfileTable) {
            test("register a profile and get it's result") {
                checkAll(userIdArb, usernameValueArb) { userId, usernameValue ->
                    assumeNotDuplicated(usernameValue)
                    newTransaction {
                        val result = with(userIdContext(userId)) { sut.register(usernameValue) }

                        assertSoftly(result) {
                            it.username shouldBe usernameValue
                            it.bio shouldBe null
                            it.image shouldBe null
                            it.following shouldBe false
                        }
                    }
                }
            }

            test("register a profile and find it with userId") {
                checkAll(userIdArb, usernameValueArb) { userId, usernameValue ->
                    assumeNotDuplicated(userId.value, usernameValue)
                    newTransaction {
                        with(userIdContext(userId)) { sut.register(usernameValue) }
                    }
                    newTransaction {
                        val result = with(userIdContext(userId)) { sut.getByUserId() }
                        assertSoftly(result) {
                            username shouldBe usernameValue
                            bio shouldBe null
                            image shouldBe null
                            following shouldBe false
                        }
                    }
                }
            }
        }
    }
}