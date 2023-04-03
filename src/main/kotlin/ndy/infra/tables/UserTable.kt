package ndy.infra.tables

import ndy.domain.user.domain.Email
import ndy.domain.user.domain.Password
import ndy.domain.user.domain.User
import ndy.domain.user.domain.UserId
import ndy.domain.user.domain.UserRepository
import ndy.infra.tables.ProfileTable.Profiles
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update

object UserTable : UserRepository {

    object Users : Table() {
        val id = ulong("id").autoIncrement()
        val email = varchar("email", Email.MAX_LENGTH)
        val password = varchar("password", 64)
        // do not use MAX_USER_PASSWORD_LENGTH since column should be encoded

        override val primaryKey = PrimaryKey(id)
    }

    override suspend fun save(user: User): User {
        val (_, email, password) = user
        val insertStatement = Users.insert {
            it[Users.email] = email.value
            it[Users.password] = password.encodedPassword
        }

        return insertStatement.resultedValues?.single()?.let(ResultRow::toUser)!!
    }

    override suspend fun findUserById(id: UserId) = Users
        .select { Users.id eq id.value }
        .map(ResultRow::toUser)
        .singleOrNull()

    override suspend fun findUserByEmail(email: Email) = Users
        .select { Users.email eq email.value }
        .map(ResultRow::toUser)
        .singleOrNull()

    override suspend fun updateById(id: UserId, email: Email?, password: Password?): Int {
        return if (listOf(email, password).any { it != null }) {
            Users.update({ Users.id eq id.value }) {
                if (email != null) it[Users.email] = email.value
                if (password != null) it[Users.password] = password.encodedPassword
            }
        } else {
            0
        }
    }

    override suspend fun findUserByIdWithProfile(id: UserId) =
        (Users innerJoin Profiles)
            .select { Users.id eq id.value }
            .map { it.toUser() to it.toProfile() }
            .singleOrNull()

    override suspend fun findUserByEmailWithProfile(email: Email) =
        (Users innerJoin Profiles)
            .select { Users.email eq email.value }
            .map { it.toUser() to it.toProfile() }
            .singleOrNull()
}
