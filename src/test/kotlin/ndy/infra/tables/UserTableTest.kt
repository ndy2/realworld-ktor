package ndy.infra.tables

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.checkAll
import ndy.domain.profile.domain.Username
import ndy.domain.user.domain.Email
import ndy.domain.user.domain.Password
import ndy.global.util.requiresNewTransaction
import ndy.test.extentions.DB
import ndy.test.spec.BaseSpec
import ndy.test.util.isNotNullOr
import ndy.test.util.shouldBeUpdatedToIf

class UserTableTest : BaseSpec(DB, body = {

    val sut = UserTable

    test("returns saved user and find it by id") {
        checkAll<Email, Password> { email, password ->
            // action
            val savedUser = requiresNewTransaction { sut.save(email, password) }

            // assert
            assertSoftly(savedUser) {
                this.id shouldNotBe null
                this.email shouldBe email
                this.password shouldBe password
            }

            // action
            val foundUser = requiresNewTransaction { sut.findUserById(savedUser.id) }

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
            val savedUserId = requiresNewTransaction { sut.save(email, password).id }

            // action
            val count = requiresNewTransaction { sut.updateById(savedUserId, updateEmail, updatePassword) }

            // assert - update count
            if (listOf(updateEmail, updatePassword).any { it != null }) count shouldBe 1
            else count shouldBe 0

            // assert - properly updated
            val foundUser = requiresNewTransaction { sut.findUserById(savedUserId) }
            assertSoftly(foundUser!!) {
                this.email shouldBeUpdatedToIf (updateEmail isNotNullOr email)
                this.password shouldBeUpdatedToIf (updatePassword isNotNullOr password)
            }
        }
    }

    test("findUserById with Profile") {
        checkAll<Email, Password, Username> { email, password, username ->
            // action
            val savedUser = requiresNewTransaction { sut.save(email, password) }
            val savedProfile = requiresNewTransaction { ProfileTable.save(savedUser.id, username) }

            // action
            val foundUser = requiresNewTransaction { sut.findUserByIdWithProfile(savedUser.id) }!!

            // assert
            assertSoftly(foundUser) {
                password.encodedPassword // invoke getter
                this.id shouldBe savedUser.id
                this.email shouldBe email
                this.password shouldBe password
            }

            foundUser.profile shouldNotBe null
            assertSoftly(foundUser.profile!!) {
                this.userId shouldBe savedUser.id
                this.username shouldBe username
            }
        }
    }

    test("save user proc") {
        requiresNewTransaction {
            val user = sut.save(Email("haha@gmail.com"), Password("1234"))
            val profile =
                ProfileTable.save(user.id, Username("haha"))

            val userWithProfile = sut.findUserByEmailWithProfile(Email("haha@gmail.com"))
            userWithProfile shouldNotBe null
            assertSoftly(userWithProfile!!) {
                this.id shouldBe user.id
                this.email shouldBe Email("haha@gmail.com")
                this.password shouldBe Password("1234")
            }
        }

    }
})
