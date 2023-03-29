package ndy.infra.tables

import ndy.domain.user.domain.*
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select

object UserTable : UserRepository {

    object Users : Table() {
        val id = ulong("id").autoIncrement()
        val email = varchar("email", MAX_USER_EMAIL_LENGTH)
        val password = varchar("password", 64)
        // do not use MAX_USER_PASSWORD_LENGTH since column should be encoded

        override val primaryKey = PrimaryKey(id)
    }

    private fun resultRowToUser(row: ResultRow): User {
        return User(
            id = UserId(row[Users.id]),
            email = Email(row[Users.email]),
            password = Password.withEncoded(row[Users.password]),
        )
    }

    override suspend fun save(
        email: Email,
        password: Password
    ): User {
        val insertStatement = Users.insert {
            it[Users.email] = email.value
            it[Users.password] = password.encodedPassword
        }

        return insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToUser)!!
    }

    override suspend fun findUserById(id: UserId) = Users
        .select { Users.id eq id.value }
        .map(::resultRowToUser)
        .singleOrNull()

    override suspend fun findUserByEmail(email: Email) = Users
        .select { Users.email eq email.value }
        .map(::resultRowToUser)
        .singleOrNull()
}
