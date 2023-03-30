package ndy.infra.tables

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.checkAll
import ndy.domain.profile.domain.Username
import ndy.domain.user.domain.Email
import ndy.domain.user.domain.Password
import ndy.test.extentions.DB
import ndy.test.spec.BaseSpec
import ndy.test.util.isNotNullOr
import ndy.test.util.shouldBeUpdatedToIf
import ndy.util.newTransaction
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

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

    test("join1") {
        val user = newTransaction { UserTable.save(Email("haha@gmail.com"), Password("password")) }
        val profile = newTransaction { ProfileTable.save(user.id, Username("haha")) }

        val result = transaction {
            (UserTable.Users innerJoin ProfileTable.Profiles)
                .select { UserTable.Users.id eq ProfileTable.Profiles.userId }
                .single()
        }

        assertSoftly(result) {
            this[UserTable.Users.id] shouldBe user.id.value
            this[UserTable.Users.password] shouldBe user.password.encodedPassword
            this[UserTable.Users.email] shouldBe user.email.value
            this[ProfileTable.Profiles.userId] shouldBe user.id.value
            this[ProfileTable.Profiles.username] shouldBe "haha"
        }

        println("result = $result")
    }

    test("join2") {
        newTransaction {
            val user = UserTable.save(Email("haha@gmail.com"), Password("password"))
            newTransaction {
                ProfileTable.save(user.id, Username("haha"))

                val result = transaction {
                    (UserTable.Users innerJoin ProfileTable.Profiles)
                        .select { UserTable.Users.id eq ProfileTable.Profiles.userId }
                        .single()
                }

                assertSoftly(result) {
                    this[UserTable.Users.id] shouldBe user.id.value
                    this[UserTable.Users.password] shouldBe user.password.encodedPassword
                    this[UserTable.Users.email] shouldBe user.email.value
                    this[ProfileTable.Profiles.userId] shouldBe user.id.value
                    this[ProfileTable.Profiles.username] shouldBe "haha"
                }

                println("result = $result")
            }
        }
    }
})
