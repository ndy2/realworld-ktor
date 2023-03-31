package ndy.infra.tables

import ndy.domain.article.domain.Article
import ndy.domain.article.domain.ArticleId
import ndy.domain.article.domain.AuthorId
import ndy.domain.profile.domain.*
import ndy.domain.tag.domain.TagId
import ndy.domain.user.domain.Email
import ndy.domain.user.domain.Password
import ndy.domain.user.domain.User
import ndy.domain.user.domain.UserId
import ndy.infra.tables.ArticleTable.Articles
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

fun resultRowAndTagIdsToArticle(row: ResultRow, tagIds: List<TagId>) = Article(
    id = ArticleId(row[Articles.id]),
    slug = row[Articles.slug],
    title = row[Articles.title],
    description = row[Articles.description],
    body = row[Articles.body],
    tagIds = tagIds,
    authorId = AuthorId(row[Articles.authorId]),
    createdAt = row[Articles.createdAt],
    updatedAt = row[Articles.updatedAt],
)