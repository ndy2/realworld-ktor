package ndy.infra.tables

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.checkAll
import ndy.domain.user.domain.Email
import ndy.domain.user.domain.Password
import ndy.test.extentions.DB
import ndy.test.spec.BaseSpec
import ndy.test.util.isNotNullOr
import ndy.test.util.shouldBeUpdatedToIf
import ndy.util.newTransaction

class UserTableTest : BaseSpec(DB, body = {

    val sut = UserTable

    test("returns saved user and find it by id") {
        checkAll<Email, Password> { email, password ->
            // action
            val savedUser = newTransaction { sut.save(email, password) }

            // assert
            assertSoftly(savedUser) {
                this.id shouldNotBe null
                this.email shouldBe email
                this.password shouldBe password
            }

            // action
            val foundUser = newTransaction { sut.findUserById(savedUser.id) }

            // assert
            assertSoftly(foundUser!!) {
                password.encodedPassword // invoke getter
                this.id shouldBe savedUser.id
                this.email shouldBe savedUser.email
                this.password shouldBe savedUser.password
            }
        }
    }

    test("update user") {
        checkAll<Email, Password, Email?, Password?> { email, password, updateEmail, updatePassword ->
            // setup
            val savedUserId = newTransaction { sut.save(email, password).id }

            // action
            val count = newTransaction { sut.updateById(savedUserId, updateEmail, updatePassword) }

            // assert - update count
            if (listOf(updateEmail, updatePassword).any { it != null }) count shouldBe 1
            else count shouldBe 0

            // assert - properly updated
            val foundUser = newTransaction { sut.findUserById(savedUserId) }
            assertSoftly(foundUser!!) {
                it.email shouldBeUpdatedToIf (updateEmail isNotNullOr email)
                it.password shouldBeUpdatedToIf (updatePassword isNotNullOr password)
            }
        }
    }
})
