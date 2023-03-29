package ndy.domain.profile.application

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.property.checkAll
import ndy.context.userIdContext
import ndy.test.extentions.DB
import ndy.test.extentions.DI
import ndy.test.extentions.JWT
import ndy.test.generator.ProfileArbs.userIdArb
import ndy.test.generator.ProfileArbs.usernameValueArb
import ndy.test.spec.BaseSpec
import ndy.util.newTransaction
import org.koin.test.inject

class ProfileServiceTest : BaseSpec(DI, DB, JWT) {

    private val sut: ProfileService by inject()

    init {
        context("register a profile") {
            checkAll(userIdArb, usernameValueArb) { userId, usernameValue ->
                test("requires a transaction") {
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

                test("fail if no transaction") {
                    val exception = shouldThrow<IllegalStateException> {
                        with(userIdContext(userId)) { sut.register(usernameValue) }
                    }

                    exception.message shouldBe "No transaction in context."
                }

                context("with registered profile") {
                    test("find its username by userId") {
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

                    test("also fail if no transaction") {
                        val exception = shouldThrow<IllegalStateException> {
                            with(userIdContext(userId)) { sut.getByUserId() }
                        }
                        exception.message shouldBe "No transaction in context."
                    }
                }
            }
        }
    }


}