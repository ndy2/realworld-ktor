package ndy.context

import ndy.domain.user.domain.UserId

interface UserIdContext {

    val userId: ULong
}

fun userIdContext(id: UserId): UserIdContext {
    return object : UserIdContext {
        override val userId = id.value
    }
}