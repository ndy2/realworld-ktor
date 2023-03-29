package ndy.domain.profile.application

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.uLong
import io.kotest.property.checkAll
import ndy.test.extentions.DB
import ndy.test.extentions.DI
import ndy.test.extentions.JWT
import ndy.test.generator.ProfileArbs.usernameValueArb
import ndy.test.spec.BaseSpec
import ndy.util.newTransaction
import org.koin.test.inject

class ProfileServiceTest : BaseSpec(DI, DB, JWT) {

    private val sut: ProfileService by inject()

    init {
        context("register a profile") {
            checkAll(Arb.uLong(), usernameValueArb) { userId, usernameValue ->
                test("requires a transaction") {
                    newTransaction {
                        val result = sut.register(userId, usernameValue)

                        assertSoftly(result) {
                            it.username shouldBe usernameValue
                            it.bio shouldBe null
                            it.image shouldBe null
                            it.following shouldBe null
                        }
                    }
                }

                test("fail if no transaction") {
                    shouldThrow<IllegalStateException> { sut.register(userId, usernameValue) }
                        .apply { message shouldBe "No transaction in context." }
                }

                context("with registered profile") {
                    test("find its username by userId") {
                        newTransaction {
                            val result = sut.getUsernameByUserId(userId)

                            result shouldBe usernameValue
                        }
                    }

                    test("also fail if no transaction") {
                        shouldThrow<IllegalStateException> { sut.getUsernameByUserId(userId) }
                            .apply { message shouldBe "No transaction in context." }
                    }
                }
            }
        }
    }


}