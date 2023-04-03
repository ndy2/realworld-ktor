package ndy.test.util

import io.kotest.property.assume
import ndy.domain.profile.domain.ProfileRepository
import ndy.domain.profile.domain.Username
import ndy.domain.user.domain.UserId

context (ProfileRepository)
suspend fun assumeNotDuplicated(username: String) {
    assume(!existByUsername(Username(username)))
}

context (ProfileRepository)
suspend fun assumeNotDuplicated(userId: ULong, username: String) {
    var duplicated = existByUsername(Username(username))
    duplicated = duplicated or (findByUserId(UserId(userId)) != null)
    assume(!duplicated)
}
