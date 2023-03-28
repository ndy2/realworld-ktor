package ndy.context

import ndy.domain.user.domain.UserId

interface AuthenticatedUserContext {

    val userId: UserId
}