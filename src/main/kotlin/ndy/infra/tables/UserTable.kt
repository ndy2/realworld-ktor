package ndy.infra.tables

import ndy.domain.user.domain.*
import org.jetbrains.exposed.sql.*

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


    override suspend fun updateById(id: UserId, email: Email?, password: Password?): Int {
        return if (listOf(email, password).any { it != null }) {
            Users.update({ Users.id eq id.value }) {
                if (email != null) it[Users.email] = email.value
                if (password != null) it[Users.password] = password.encodedPassword
            }
        } else 0
    }
}
