package ndy.infra.tables

import ndy.domain.profile.domain.*
import ndy.domain.user.domain.UserId
import org.jetbrains.exposed.sql.*

object ProfileTable : ProfileRepository {

    object Profiles : Table() {
        val id = ulong("id").autoIncrement()
        val userId = ulong("userId")
        val username = varchar("username", 64).uniqueIndex()
        val bio = varchar("bio", 512).nullable()
        val image = varchar("image", 256).nullable()

        override val primaryKey = PrimaryKey(id)
    }

    private fun resultRowToProfile(row: ResultRow): Profile {
        return Profile(
            id = ProfileId(row[Profiles.id]),
            username = Username(row[Profiles.username]),
            bio = row[Profiles.bio]?.let { Bio(it) },
            image = row[Profiles.image]?.let { Image.ofFullPath(it) }
        )
    }

    override suspend fun save(userId: UserId, username: Username): Profile {
        val insertStatement = Profiles.insert {
            it[Profiles.userId] = userId.value
            it[Profiles.username] = username.value
        }

        return insertStatement.resultedValues?.singleOrNull()?.let(ProfileTable::resultRowToProfile)!!
    }

    override suspend fun findById(id: ProfileId) = Profiles
        .select { Profiles.id eq id.value }
        .map(::resultRowToProfile)
        .singleOrNull()

    override suspend fun findByUserId(userId: UserId) = Profiles
        .select { Profiles.userId eq userId.value }
        .map(::resultRowToProfile)
        .singleOrNull()

    override suspend fun updateById(userId: UserId, username: Username?, bio: Bio?, image: Image?) = Profiles
        .update({ Profiles.userId eq userId.value }) {
            if (username != null) it[Profiles.username] = username.value
            if (bio != null) it[Profiles.bio] = bio.value
            if (image != null) it[Profiles.image] = image.fullPath
        }
}