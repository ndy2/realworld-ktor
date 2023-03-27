package ndy.infra.tables

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.PropertyTesting
import io.kotest.property.checkAll
import ndy.domain.user.domain.Email
import ndy.domain.user.domain.Password
import ndy.test.extentions.DB
import ndy.test.spec.BaseSpec
import ndy.util.newTransaction
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

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
                    password.encodedPassword

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
})
