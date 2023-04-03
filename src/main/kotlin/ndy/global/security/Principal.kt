package ndy.global.security

import ndy.domain.profile.domain.ProfileId
import ndy.domain.user.domain.UserId

/**
 * Principal for authenticated user
 *
 * - created by JwtAuth#validate @plugins.Security
 * - principal would be converted to `AuthenticatedUserContext` by `authenticatedXXX`
 * @see ndy.plugins.configureSecurity
 * @see ndy.global.context.AuthenticatedUserContext
 */
data class Principal(
        val userId: UserId,
        val profileId: ProfileId
) : io.ktor.server.auth.Principal
