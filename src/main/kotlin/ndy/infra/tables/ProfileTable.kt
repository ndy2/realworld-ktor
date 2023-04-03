package ndy.infra.tables

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update
import ndy.domain.profile.domain.Bio
import ndy.domain.profile.domain.Image
import ndy.domain.profile.domain.Profile
import ndy.domain.profile.domain.ProfileId
import ndy.domain.profile.domain.ProfileRepository
import ndy.domain.profile.domain.Username
import ndy.domain.user.domain.UserId
import ndy.infra.tables.UserTable.Users

object ProfileTable : ProfileRepository {

    object Profiles : Table() {
        val id = ulong("id").autoIncrement()
        val userId = ulong("userid").references(Users.id)
        val username = varchar("username", Username.MAX_LENGTH).uniqueIndex()
        val bio = varchar("bio", Bio.MAX_LENGTH).nullable()
        val image = varchar("image", Image.MAX_LENGTH).nullable()

        override val primaryKey = PrimaryKey(id)
    }

    override suspend fun save(userId: UserId, username: Username): Profile {
        val insertStatement = Profiles.insert {
            it[Profiles.userId] = userId.value
            it[Profiles.username] = username.value
        }

        return insertStatement.resultedValues?.singleOrNull()?.let(ResultRow::toProfile)!!
    }

    override suspend fun findById(id: ProfileId) = Profiles
            .select { Profiles.id eq id.value }
            .map(ResultRow::toProfile)
            .singleOrNull()

    override suspend fun findByUserId(userId: UserId) = Profiles
            .select { Profiles.userId eq userId.value }
            .map(ResultRow::toProfile)
            .singleOrNull()

    override suspend fun updateByUserId(userId: UserId, username: Username?, bio: Bio?, image: Image?): Int {
        return if (listOf(username, bio, image).any { it != null }) {
            Profiles.update({ Profiles.userId eq userId.value }) {
                if (username != null) it[Profiles.username] = username.value
                if (bio != null) it[Profiles.bio] = bio.value
                if (image != null) it[Profiles.image] = image.fullPath
            }
        } else {
            0
        }
    }

    override suspend fun existByUsername(username: Username) = Profiles
            .select { Profiles.username eq username.value }
            .empty().not()

    override suspend fun findProfileByUsername(username: Username) = Profiles
            .select { Profiles.username eq username.value }
            .map(ResultRow::toProfile)
            .singleOrNull()
}
