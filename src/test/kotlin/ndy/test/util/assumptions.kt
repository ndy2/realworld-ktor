package ndy.test.util

import io.kotest.property.assume
import ndy.domain.profile.domain.ProfileRepository
import ndy.domain.profile.domain.Username
import ndy.util.newTransaction

suspend fun assumeNonDuplicatedUsername(username: String, profileRepository: ProfileRepository){
    newTransaction {
        assume(!profileRepository.existByUsername(Username(username)))
    }
}