package ndy.infra.tables

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.checkAll
import ndy.domain.profile.domain.Username
import ndy.domain.user.domain.Email
import ndy.domain.user.domain.Password
import ndy.domain.user.domain.User
import ndy.test.extentions.DB
import ndy.test.spec.BaseSpec
import ndy.test.util.isNotNullOr
import ndy.test.util.shouldBeUpdatedToIf

class UserTableTest : BaseSpec(DB, body = {

    val sut = UserTable

    transactionalTest("returns saved user and find it by id") {
        checkAll<User> { user ->
            // action
            val savedUser = sut.save(user)

            // assert
            val (_, email, password) = user
            assertSoftly(savedUser) {
                this.id shouldNotBe null
                this.email shouldBe email
                this.password shouldBe password
            }

            // action
            val foundUser = sut.findUserById(savedUser.id)

            // assert
            assertSoftly(foundUser!!) {
                password.encodedPassword // invoke getter
                this.id shouldBe savedUser.id
                this.email shouldBe savedUser.email
                this.password shouldBe savedUser.password
            }
        }
    }

    transactionalTest("update user") {
        checkAll<User, Email?, Password?> { user, updateEmail, updatePassword ->
            // setup
            val savedUserId = sut.save(user).id

            // action
            val count = sut.updateById(savedUserId, updateEmail, updatePassword)

            // assert - update count
            if (listOf(updateEmail, updatePassword).any { it != null }) count shouldBe 1
            else count shouldBe 0

            // assert - properly updated
            val foundUser = sut.findUserById(savedUserId)
            assertSoftly(foundUser!!) {
                this.email shouldBeUpdatedToIf (updateEmail isNotNullOr email)
                this.password shouldBeUpdatedToIf (updatePassword isNotNullOr password)
            }
        }
    }

    transactionalTest("findUserById with Profile") {
        checkAll<User, Username> { user, username ->
            // setup
            val savedUser = sut.save(user)
            val savedProfile = ProfileTable.save(savedUser.id, username)

            // action
            val (foundUser, profile) = sut.findUserByIdWithProfile(savedUser.id)!!

            // assert
            assertSoftly(foundUser) {
                password.encodedPassword // invoke getter
                this.id shouldBe savedUser.id
                this.email shouldBe email
                this.password shouldBe password
            }

            assertSoftly(profile) {
                this.userId shouldBe savedUser.id
                this.username shouldBe username
            }
        }
    }

    transactionalTest("findUserByEmailWithProfile") {
        checkAll<User, Username> { user, username ->
            // action
            val savedUser = sut.save(user)
            val savedProfile = ProfileTable.save(savedUser.id, username)

            // action
            val (findUser, profile) = sut.findUserByEmailWithProfile(user.email)!!

            // assert
            assertSoftly(findUser) {
                password.encodedPassword // invoke getter
                this.id shouldBe savedUser.id
                this.email shouldBe savedUser.email
                this.password shouldBe savedUser.password
            }

            assertSoftly(profile) {
                this.userId shouldBe savedUser.id
                this.username shouldBe username
            }
        }
    }
})
