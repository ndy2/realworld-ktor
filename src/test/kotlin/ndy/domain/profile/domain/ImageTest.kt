package ndy.domain.profile.domain

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import io.kotest.property.checkAll
import ndy.test.generator.ProfileArbs.imageExtensionArb
import ndy.test.generator.ProfileArbs.imageFilNameArb
import ndy.test.generator.ProfileArbs.imageStorePathArb
import ndy.test.spec.BaseSpec

class ImageTest : BaseSpec(body = {

    test("create image, get full path & create image using full path") {
        checkAll(
            imageStorePathArb,
            imageFilNameArb,
            imageExtensionArb
        ) { storePath, fileName, extension ->
            val image = Image(storePath, fileName, extension)
            assertSoftly(image) {
                this.storePath shouldBe storePath
                this.fileName shouldBe fileName
                this.extension shouldBe extension
                this.fullPath shouldBe "$storePath/$fileName.$extension"
            }

            val fullPath = "$storePath/$fileName.$extension"
            Image.ofFullPath(fullPath) shouldBe image
        }
    }
})