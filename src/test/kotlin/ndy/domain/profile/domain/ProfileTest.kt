package ndy.domain.profile.domain

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import io.kotest.property.checkAll
import ndy.domain.user.domain.UserId
import ndy.test.spec.BaseSpec

class ProfileTest : BaseSpec(body = {

    test("create profile") {
        checkAll<ULong, UserId, Username, Bio, Image> { id, userId, username, bio, image ->
            // setup
            val profile = Profile(ProfileId(id), userId, username, bio, image)

            // assert
            assertSoftly(profile) {
                this.id.value shouldBe id
                this.userId shouldBe userId
                this.username shouldBe username
                this.bio shouldBe bio
                this.image shouldBe image
            }
        }
    }
})