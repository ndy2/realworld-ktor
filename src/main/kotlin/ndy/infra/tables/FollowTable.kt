package ndy.infra.tables

import ndy.domain.profile.domain.ProfileId
import ndy.domain.profile.follow.domain.FollowRepository
import ndy.domain.user.domain.UserId
import ndy.infra.tables.ProfileTable.Profiles
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select

object FollowTable : FollowRepository {

    object Follows : Table() {
        val followerId = ulong("follower_id").references(Profiles.id)
        val followeeId = ulong("followee_id").references(Profiles.id)
    }

    override suspend fun save(followerId: ProfileId, followeeId: ProfileId) {
        Follows.insert {
            it[Follows.followerId] = followerId.value
            it[Follows.followeeId] = followeeId.value
        }
    }

    override suspend fun delete(followerId: ProfileId, followeeId: ProfileId) {
        Follows.deleteWhere {
            Follows.followerId eq followerId.value
            Follows.followeeId eq followeeId.value
        }
    }

    override suspend fun exists(followerId: ProfileId, followeeId: ProfileId) = Follows
        .select {
            Follows.followerId eq followerId.value
            Follows.followeeId eq followeeId.value
        }
        .empty().not()
}