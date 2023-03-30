package ndy.infra.tables

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.PropertyTesting
import io.kotest.property.checkAll
import ndy.domain.user.domain.Email
import ndy.domain.user.domain.Password
import ndy.domain.user.domain.UserId
import ndy.test.extentions.DB
import ndy.test.spec.BaseSpec
import ndy.test.util.isNotNullOr
import ndy.test.util.shouldBeUpdatedToIf
import ndy.util.newTransaction
import org.jetbrains.exposed.sql.selectAll

class UserTableTest : BaseSpec(DB, body = {

    val sut = UserTable

    test("returns saved user and find it by id") {
        checkAll<Email, Password> { email, password ->
            newTransaction {
                val savedUser = sut.save(email, password)
                assertSoftly(savedUser) {
                    this.id shouldNotBe null
                    this.email shouldBe email
                    this.password shouldBe password
                }

                val foundUser = sut.findUserById(savedUser.id)
                assertSoftly(foundUser!!) {
                    password.encodedPassword // invoke getter
                    this.id shouldBe savedUser.id
                    this.email shouldBe savedUser.email
                    this.password shouldBe savedUser.password
                }
            }
        }

        newTransaction {
            val count = UserTable.Users.selectAll().count()
            count shouldBe PropertyTesting.defaultIterationCount
        }
    }

    test("update user") {
        checkAll<Email, Password, Email?, Password?> { email, password, updateEmail, updatePassword ->
            var savedUserId = UserId(0u)
            newTransaction {
                savedUserId = sut.save(email, password).id
                val count = sut.updateById(savedUserId, updateEmail, updatePassword)

                if (listOf(updateEmail, updatePassword).any { it != null }) count shouldBe 1
                else count shouldBe 0
            }

            newTransaction {
                val foundUser = sut.findUserById(savedUserId)
                assertSoftly(foundUser!!) {
                    it.email shouldBeUpdatedToIf (updateEmail isNotNullOr email)
                    it.password shouldBeUpdatedToIf (updatePassword isNotNullOr password)
                }
            }
        }
    }
})
