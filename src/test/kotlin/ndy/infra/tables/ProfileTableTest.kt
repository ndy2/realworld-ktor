package ndy.infra.tables

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.assume
import io.kotest.property.checkAll
import ndy.domain.profile.domain.Bio
import ndy.domain.profile.domain.Image
import ndy.domain.profile.domain.Username
import ndy.domain.user.domain.UserId
import ndy.test.extentions.DB
import ndy.test.spec.BaseSpec
import ndy.test.util.isNotNullOr
import ndy.test.util.shouldBeUpdatedToIf
import ndy.util.newTransaction

class ProfileTableTest : BaseSpec(DB, body = {

    val sut = ProfileTable

    context("prop1") {
        checkAll<ULong, Username> { userId, username ->
            test("returns saved profile and find it") {
                newTransaction {
                    assume(!sut.existByUsername(username))
                    val savedProfile = sut.save(UserId(userId), username)
                    assertSoftly(savedProfile) {
                        this.id shouldNotBe null
                        this.username shouldBe username
                        this.bio shouldBe null
                        this.image shouldBe null
                    }

                    val foundProfile = sut.findById(savedProfile.id)
                    assertSoftly(foundProfile!!) {
                        this.id shouldBe savedProfile.id
                        this.username shouldBe savedProfile.username
                        this.bio shouldBe savedProfile.bio
                        this.image shouldBe savedProfile.image
                    }
                }
            }
        }
    }

    context("prop2") {
        checkAll<UserId, Username, Bio?, Image?, Username?> { userId, username, updateBio, updateImage, updateUsername ->
            var duplicated = newTransaction { sut.existByUsername(username) }
            if (updateUsername != null) {
                duplicated = duplicated or newTransaction { sut.existByUsername(updateUsername) }
                duplicated = duplicated or (duplicated.equals(username))
            }
            assume(!duplicated)

            test("update profile") {
                newTransaction {
                    sut.save(userId, username)
                    val count = sut.updateByUserId(userId, updateUsername, updateBio, updateImage)

                    if (listOf(updateBio, updateImage, updateUsername).any { it != null }) count shouldBe 1
                    else count shouldBe 0
                }


                newTransaction {

                    val foundProfile = sut.findByUserId(userId)
                    assertSoftly(foundProfile!!) {
                        it.username shouldBeUpdatedToIf (updateUsername isNotNullOr username)
                        it.bio shouldBe updateBio
                        it.image shouldBe updateImage
                    }
                }
                println("username : $username")
            }
        }
    }

    context("prop3") {
        checkAll<UserId, Username> { userId, username ->
            test("exist by username") {
                newTransaction {
                    sut.save(userId, username)
                }

                newTransaction {
                    sut.existByUsername(username) shouldBe true
                    sut.existByUsername(Username("nonExist${username.value}")) shouldBe false
                }
            }
        }
    }

})