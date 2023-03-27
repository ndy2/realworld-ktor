package ndy.infra.tables

import ndy.domain.user.domain.*
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select

object UserTable : UserRepository {

    object Users : Table() {
        val id = ulong("id").autoIncrement()
        val username = varchar("username", 128)
        val email = varchar("email", 128)
        val password = varchar("password", 512)

        override val primaryKey = PrimaryKey(id)
    }

    private fun resultRowToUser(row: ResultRow): User {
        return User(
            id = row[Users.id],
            username = Username(row[Users.username]),
            email = Email(row[Users.email]),
            password = Password.withEncoded(row[Users.password]),
        )
    }

    override suspend fun save(
        username: Username,
        email: Email,
        password: Password
    ): User {
        val insertStatement = Users.insert {
            it[Users.username] = username.value
            it[Users.email] = email.value
            it[Users.password] = password.encodedPassword
        }

        return insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToUser)!!
    }

    override suspend fun findUserById(id: ULong) = Users
        .select { Users.id eq id }
        .map(::resultRowToUser)
        .singleOrNull()

    override suspend fun findUserByEmail(email: Email) = Users
        .select { Users.email eq email.value }
        .map(::resultRowToUser)
        .singleOrNull()
}
