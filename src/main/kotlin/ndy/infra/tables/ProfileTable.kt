package ndy.infra.tables

import ndy.domain.profile.domain.*
import ndy.domain.user.domain.UserId
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select

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

    override suspend fun findUsernameByUserId(userId: UserId) = Profiles
        .slice(Profiles.username)
        .select { Profiles.userId eq userId.value }
        .map { it[Profiles.username] }
        .single()
}