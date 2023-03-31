package ndy.infra.tables

import ndy.domain.profile.domain.*
import ndy.domain.user.domain.Email
import ndy.domain.user.domain.Password
import ndy.domain.user.domain.User
import ndy.domain.user.domain.UserId
import ndy.infra.tables.ProfileTable.Profiles
import ndy.infra.tables.UserTable.Users
import org.jetbrains.exposed.sql.ResultRow

fun resultRowToUser(row: ResultRow) = User(
    id = UserId(row[Users.id]),
    email = Email(row[Users.email]),
    password = Password.withEncoded(row[Users.password]),
)

fun resultRowToUserWithProfile(row: ResultRow) = User(
    id = UserId(row[Users.id]),
    email = Email(row[Users.email]),
    password = Password.withEncoded(row[Users.password]),
    profile = resultRowToProfile(row)
)

fun resultRowToProfile(row: ResultRow) = Profile(
    id = ProfileId(row[Profiles.id]),
    userId = UserId(row[Profiles.userId]),
    username = Username(row[Profiles.username]),
    bio = row[Profiles.bio]?.let { Bio(it) },
    image = row[Profiles.image]?.let { Image.ofFullPath(it) },
)