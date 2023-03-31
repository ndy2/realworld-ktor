package ndy.infra.tables

import ndy.domain.article.domain.Article
import ndy.domain.article.domain.ArticleId
import ndy.domain.article.domain.AuthorId
import ndy.domain.profile.domain.*
import ndy.domain.tag.domain.Tag
import ndy.domain.tag.domain.TagId
import ndy.domain.user.domain.Email
import ndy.domain.user.domain.Password
import ndy.domain.user.domain.User
import ndy.domain.user.domain.UserId
import ndy.infra.tables.ArticleTable.Articles
import ndy.infra.tables.ProfileTable.Profiles
import ndy.infra.tables.TagTable.Tags
import ndy.infra.tables.UserTable.Users
import org.jetbrains.exposed.sql.ResultRow

fun ResultRow.toUser() = User(
    id = UserId(this[Users.id]),
    email = Email(this[Users.email]),
    password = Password.withEncoded(this[Users.password]),
)

fun ResultRow.toUserWithProfile() = User(
    id = UserId(this[Users.id]),
    email = Email(this[Users.email]),
    password = Password.withEncoded(this[Users.password]),
    profile = this.toProfile()
)

fun ResultRow.toProfile() = Profile(
    id = ProfileId(this[Profiles.id]),
    userId = UserId(this[Profiles.userId]),
    username = Username(this[Profiles.username]),
    bio = this[Profiles.bio]?.let { Bio(it) },
    image = this[Profiles.image]?.let { Image.ofFullPath(it) },
)

fun ResultRow.toArticle(tagIds: List<TagId>) = Article(
    id = ArticleId(this[Articles.id]),
    slug = this[Articles.slug],
    title = this[Articles.title],
    description = this[Articles.description],
    body = this[Articles.body],
    tagIds = tagIds,
    authorId = AuthorId(this[Articles.authorId]),
    createdAt = this[Articles.createdAt],
    updatedAt = this[Articles.updatedAt],
)

fun ResultRow.toTag() = Tag(
    id = TagId(this[Tags.id]),
    name = this[Tags.name],
)