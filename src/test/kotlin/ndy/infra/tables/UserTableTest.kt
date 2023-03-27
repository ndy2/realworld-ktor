package ndy.infra.tables

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.PropertyTesting
import io.kotest.property.checkAll
import ndy.domain.user.domain.Email
import ndy.domain.user.domain.Password
import ndy.domain.user.domain.Username
import ndy.test.extentions.DB
import ndy.test.spec.BaseSpec
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class UserTableTest : BaseSpec(DB, body = {

    val sut = UserTable()
    test("returns saved user and find it by id") {
        checkAll<Username, Email, Password> { username, email, password ->
            val savedUser = sut.save(username, email, password)
            assertSoftly(savedUser) {

                this.id shouldNotBe null
                this.username shouldBe username
                this.email shouldBe email
                this.password shouldBe password
            }

            val foundUser = sut.findUserById(savedUser.id)
            assertSoftly(foundUser!!) {
                password.encodedPassword

                this.id shouldBe savedUser.id
                this.username shouldBe savedUser.username
                this.email shouldBe savedUser.email
                this.password shouldBe savedUser.password
            }
        }

        transaction {
            val count = UserTable.Users.selectAll().count()
            count shouldBe PropertyTesting.defaultIterationCount
        }
    }
})
