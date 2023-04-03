package ndy.infra.tables

import ndy.domain.profile.domain.ProfileId
import ndy.domain.profile.follow.domain.FollowRepository
import ndy.global.util.deleteWhere
import ndy.global.util.selectWhere
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert

object FollowTable : FollowRepository {

    object Follows : Table() {
        val followerId = ulong("follower_id")/*.references(Profiles.id)*/
        val followeeId = ulong("followee_id")/*.references(Profiles.id)*/
    }

    override suspend fun save(followerId: ProfileId, followeeId: ProfileId) {
        Follows.insert {
            it[Follows.followerId] = followerId.value
            it[Follows.followeeId] = followeeId.value
        }
    }

    override suspend fun delete(followerId: ProfileId, followeeId: ProfileId) {
        Follows.deleteWhere(
            Follows.followerId eq followerId.value,
            Follows.followeeId eq followeeId.value
        )
    }

    override suspend fun exists(followerId: ProfileId, followeeId: ProfileId) = Follows
        .selectWhere(
            Follows.followerId eq followerId.value,
            Follows.followeeId eq followeeId.value
        )
        .empty().not()

    override suspend fun existsList(followerId: ProfileId, followeeIds: List<ProfileId>): List<Boolean> {
        // find all followee ids
        val allFolloweeIds = Follows
            .selectWhere(
                Follows.followerId eq followerId.value,
                Follows.followeeId inList followeeIds.map(ProfileId::value)
            )
            .map { ProfileId(it[Follows.followeeId]) }
            .toSet()

        // map given followee ids to check following
        return followeeIds.map { allFolloweeIds.contains(it) }
    }
}
