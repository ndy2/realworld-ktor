package ndy.test.util

import io.kotest.property.assume
import ndy.domain.profile.domain.ProfileRepository
import ndy.domain.profile.domain.Username
import ndy.domain.user.domain.UserId
import ndy.util.newTransaction


context (ProfileRepository)
suspend fun assumeNotDuplicated(username: String) {
    newTransaction {
        assume(!existByUsername(Username(username)))
    }
}

context (ProfileRepository)
suspend fun assumeNotDuplicated(userId: ULong, username: String) {
    var duplicated = newTransaction { existByUsername(Username(username)) }
    duplicated = duplicated or (newTransaction { findByUserId(UserId(userId)) } != null)
    assume(!duplicated)
}