package ndy.domain.profile.domain

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import io.kotest.property.checkAll
import ndy.test.spec.BaseSpec

class ProfileTest : BaseSpec(body = {

    test("create profile") {
        checkAll<ULong, Username, Bio, Image> { id, username, bio, image ->
            // setup
            val profile = Profile(ProfileId(id), username, bio, image)

            // assert
            assertSoftly(profile){
                this.id.value shouldBe id
                this.username shouldBe username
                this.bio shouldBe bio
                this.image shouldBe image
            }
        }
    }
})