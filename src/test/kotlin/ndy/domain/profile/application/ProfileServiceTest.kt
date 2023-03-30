package ndy.domain.profile.application

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import io.kotest.property.checkAll
import ndy.context.userIdContext
import ndy.infra.tables.ProfileTable
import ndy.test.extentions.DB
import ndy.test.generator.ProfileArbs.userIdArb
import ndy.test.generator.ProfileArbs.usernameValueArb
import ndy.test.spec.BaseSpec
import ndy.test.util.assumeNotDuplicated
import ndy.util.newTransaction

class ProfileServiceTest : BaseSpec(DB, body = {

    val sut = ProfileService(ProfileTable)
    with(ProfileTable) {

        test("register a profile and get it's result") {
            checkAll(userIdArb, usernameValueArb) { userId, usernameValue ->
                // setup
                assumeNotDuplicated(usernameValue)

                // action
                val result = newTransaction { with(userIdContext(userId)) { sut.register(usernameValue) } }

                // assert
                assertSoftly(result) {
                    it.username shouldBe usernameValue
                    it.bio shouldBe null
                    it.image shouldBe null
                    it.following shouldBe false
                }
            }
        }

        test("register a profile and find it with userId") {
            checkAll(userIdArb, usernameValueArb) { userId, usernameValue ->
                // setup
                assumeNotDuplicated(userId.value, usernameValue)
                newTransaction { with(userIdContext(userId)) { sut.register(usernameValue) } }

                // action
                val result = newTransaction { with(userIdContext(userId)) { sut.getByUserId() } }

                // assert
                assertSoftly(result) {
                    username shouldBe usernameValue
                    bio shouldBe null
                    image shouldBe null
                    following shouldBe false
                }
            }
        }

        // TODO - write test code
        xtest("update profile") {}
    }
})