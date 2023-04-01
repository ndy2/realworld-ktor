package ndy.test.util

import io.kotest.property.assume
import ndy.domain.profile.domain.ProfileRepository
import ndy.domain.profile.domain.Username
import ndy.domain.user.domain.UserId
import ndy.global.util.requiresNewTransaction


context (ProfileRepository)
suspend fun assumeNotDuplicated(username: String) {
    requiresNewTransaction {
        assume(!existByUsername(Username(username)))
    }
}

context (ProfileRepository)
suspend fun assumeNotDuplicated(userId: ULong, username: String) {
    var duplicated = requiresNewTransaction { existByUsername(Username(username)) }
    duplicated = duplicated or (requiresNewTransaction { findByUserId(UserId(userId)) } != null)
    assume(!duplicated)
}