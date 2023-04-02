package ndy.domain.profile.domain

import io.kotest.assertions.konform.shouldBeValid
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.property.checkAll
import ndy.global.exception.RealworldRuntimeException
import ndy.test.generator.ProfileArbs.imageArb
import ndy.test.generator.ProfileArbs.imageExtensionArb
import ndy.test.generator.ProfileArbs.imageFileNameArb
import ndy.test.generator.ProfileArbs.imageFullPathArb
import ndy.test.generator.ProfileArbs.imageStorePathArb
import ndy.test.spec.BaseSpec

class ImageTest : BaseSpec(body = {

    test("imageArb generates valid image") {
        checkAll(imageArb) { validateImage shouldBeValid it }
    }

    test("image validation work properly") {
        checkAll(imageFullPathArb) { shouldNotThrow<RealworldRuntimeException> { Image.ofFullPath(it) } }
        checkAll<String> { shouldThrow<RealworldRuntimeException> { Image.ofFullPath(it) } }
    }

    test("storePath/fileName/extension split properly") {
        checkAll(
            imageStorePathArb,
            imageFileNameArb,
            imageExtensionArb
        ) { storePath, fileName, extension ->
            // setup
            val fullPath = "$storePath/$fileName.$extension"

            // action
            val image = Image.ofFullPath(fullPath)

            // assert
            image.storePath shouldBe storePath
            image.fileName shouldBe fileName
            image.extension shouldBe extension
        }
    }
})